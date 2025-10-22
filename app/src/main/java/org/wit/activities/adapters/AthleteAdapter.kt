package org.wit.activities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.activities.databinding.CardAthleteBinding
import org.wit.activities.models.AthleteModel

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

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val athlete = athletes[holder.adapterPosition]
        holder.bind(athlete, listener)
    }

    override fun getItemCount(): Int = athletes.size

    class MainHolder(private val binding: CardAthleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(athlete: AthleteModel, listener: AthleteListener) {
            binding.athleteName.text = athlete.title
            binding.athleteNotes.text = athlete.description

            binding.root.setOnClickListener {
                listener.onAthleteClick(athlete)
            }
        }
    }
}
