package org.wit.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.activities.databinding.CardAthleteBinding
import org.wit.activities.models.AthleteModel

class AthleteAdapter(
    private val athletes: List<AthleteModel>,
    private val listener: AthleteListener
) : RecyclerView.Adapter<AthleteAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardAthleteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val athlete = athletes[position]
        holder.bind(athlete, listener)
    }

    override fun getItemCount(): Int = athletes.size

    fun getItem(position: Int): AthleteModel = athletes[position]

    class MainHolder(private val binding: CardAthleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(athlete: AthleteModel, listener: AthleteListener) {
            binding.athleteName.text = athlete.name
            binding.athleteNotes.text = athlete.description
            binding.athleteRole.text = athlete.role
            binding.athleteGroup.text = athlete.group
            binding.athleteCountry.text = athlete.country.displayName
            binding.athletePB.text = formatPb(athlete)
            binding.athleteActive.text = if (athlete.isActive) "Active" else "Inactive"
            binding.root.setOnClickListener {
                listener.onAthleteClick(athlete)
            }
        }

        private fun formatPb(athlete: AthleteModel): String {
            val totalSeconds = athlete.personalBestSeconds
            if (totalSeconds == null) return "PB: N/A"

            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val time = String.format("%d:%02d", minutes, seconds)

            return "PB: $time"
        }
    }
}
