package org.wit.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.activities.databinding.CardAthleteBinding
import org.wit.activities.models.AthleteModel

class AthleteAdapter(
    athletes: List<AthleteModel>,
    private val listener: AthleteListener
) : RecyclerView.Adapter<AthleteAdapter.MainHolder>() {

    private val athletes = athletes.toMutableList()


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

    fun setData(newAthletes: List<AthleteModel>) {
        athletes.clear()
        athletes.addAll(newAthletes)
        notifyDataSetChanged()
    }

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
            val pb = athlete.personalBestSeconds ?: return "PB: N/A"
            val event = athlete.event

            val sprintEvents = listOf("100m", "200m", "400m")
            val longEvents = listOf("Half Marathon", "Marathon")

            val timeString = when {
                event in sprintEvents -> {
                    val seconds = pb / 100f
                    "%.2f".format(seconds)
                }
                event in longEvents -> {
                    val h = pb / 3600
                    val m = (pb % 3600) / 60
                    val s = pb % 60
                    "%02d:%02d:%02d".format(h, m, s)
                }
                else -> {
                    val minutes = pb / 60
                    val seconds = pb % 60
                    "%d:%02d".format(minutes, seconds)
                }
            }

            return if (event.isNotBlank()) {
                "PB: $timeString ($event)"
            } else {
                "PB: $timeString"
            }
        }
    }
}
