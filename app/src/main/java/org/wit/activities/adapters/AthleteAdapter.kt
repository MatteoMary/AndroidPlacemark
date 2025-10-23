package org.wit.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.activities.databinding.CardAthleteBinding
import org.wit.activities.models.AthleteModel
import androidx.recyclerview.widget.ItemTouchHelper
interface AthleteListener {
    fun onAthleteClick(athlete: AthleteModel)
}

class AthleteAdapter(
private var athletes: List<AthleteModel>,
private val listener: AthleteListener
) : RecyclerView.Adapter<AthleteAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardAthleteBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    fun getItem(position: Int): AthleteModel = athletes[position]

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val athlete = athletes[holder.adapterPosition]
        holder.bind(athlete, listener)
    }

    override fun getItemCount(): Int = athletes.size

    class MainHolder(private val binding: CardAthleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(athlete: AthleteModel, listener: AthleteListener) {
            binding.athleteName.text = athlete.name
            binding.athleteNotes.text = athlete.description
            binding.athleteRole.text = athlete.role
            binding.athleteGroup.text = athlete.group
            binding.athleteCountry.text = athlete.country
            binding.athletePB.text = if (athlete.personalBest.isNotBlank()) "PB: ${athlete.personalBest}" else ""
            binding.athleteActive.text = if (athlete.isActive) "Active" else "Inactive"
            binding.root.setOnClickListener { listener.onAthleteClick(athlete) }
        }

    }
}
