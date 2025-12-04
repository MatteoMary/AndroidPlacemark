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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.R
import org.wit.activities.adapters.AthleteAdapter
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

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as AthleteAdapter
                val pos = viewHolder.bindingAdapterPosition
                val athlete = adapter.getItem(pos)

                app.athletes.delete(athlete)

                binding.recyclerView.adapter = AthleteAdapter(app.athletes.findAll(), this@AthleteListActivity)
                toggleEmptyState()

                Snackbar.make(binding.root, "Deleted ${athlete.name}", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        app.athletes.create(athlete.copy(id = 0))
                        binding.recyclerView.adapter = AthleteAdapter(app.athletes.findAll(), this@AthleteListActivity)
                        toggleEmptyState()
                    }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

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