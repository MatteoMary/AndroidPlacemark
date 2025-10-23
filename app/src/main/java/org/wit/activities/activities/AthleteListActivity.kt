package org.wit.activities.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wit.activities.R
import org.wit.activities.adapters.AthleteListener
import org.wit.activities.databinding.ActivityAthleteListBinding
import org.wit.activities.databinding.CardAthleteBinding
import org.wit.activities.main.MainApp
import org.wit.activities.models.AthleteModel

class AthleteListActivity : AppCompatActivity(), AthleteListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityAthleteListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAthleteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = AthleteAdapter(app.athletes.findAll(), this)
        toggleEmptyState()

        binding.fabAddAthlete.setOnClickListener {
            val intent = Intent(this, AthleteActivity::class.java)
            getResult.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add -> {
                val intent = Intent(this, AthleteActivity::class.java)
                getResult.launch(intent)
                true
            }
            R.id.item_cancel -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                binding.recyclerView.adapter = AthleteAdapter(app.athletes.findAll(), this)
                toggleEmptyState()
            }
        }

    override fun onAthleteClick(athlete: AthleteModel) {
        val intent = Intent(this, AthleteActivity::class.java)
        intent.putExtra("athlete_edit", athlete)
        getResult.launch(intent)
    }

    private fun toggleEmptyState() {
        val empty = app.athletes.findAll().isEmpty()
        binding.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (empty) View.GONE else View.VISIBLE
    }
}

class AthleteAdapter(
    private var athletes: List<AthleteModel>,
    private val listener: AthleteListener
) : RecyclerView.Adapter<AthleteAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardAthleteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val athlete = athletes[holder.bindingAdapterPosition]
        holder.bind(athlete, listener)
    }

    override fun getItemCount(): Int = athletes.size

    class MainHolder(private val binding: CardAthleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(athlete: AthleteModel, listener: AthleteListener) {
            binding.athleteName.text = athlete.title
            binding.athleteNotes.text = athlete.description
            binding.athleteRole.text = athlete.role
            binding.root.setOnClickListener { listener.onAthleteClick(athlete) }
        }
    }
}
