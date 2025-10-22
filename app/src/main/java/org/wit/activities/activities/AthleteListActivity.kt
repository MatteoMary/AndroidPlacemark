package org.wit.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
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
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = AthleteAdapter(app.athletes.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, AthleteActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                // Rebind with fresh data from the store
                binding.recyclerView.adapter = AthleteAdapter(app.athletes.findAll(), this)
                // or: (binding.recyclerView.adapter as? AthleteAdapter)?.notifyDataSetChanged()
            }
        }

    override fun onAthleteClick(athlete: AthleteModel) {
        val launcherIntent = Intent(this, AthleteActivity::class.java)
        launcherIntent.putExtra("athlete_edit", athlete)
        getResult.launch(launcherIntent)
    }
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
        val athlete = athletes[holder.bindingAdapterPosition]
        holder.bind(athlete, listener)
    }

    override fun getItemCount(): Int = athletes.size

    class MainHolder(private val binding: CardAthleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(athlete: AthleteModel, listener: AthleteListener) {
            binding.athleteName.text = athlete.title
            binding.athleteNotes.text = athlete.description
            binding.root.setOnClickListener { listener.onAthleteClick(athlete) }
        }
    }
}
