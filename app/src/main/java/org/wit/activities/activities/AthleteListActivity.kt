package org.wit.activities.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.R
import org.wit.activities.adapters.AthleteAdapter
import org.wit.activities.adapters.AthleteListener
import org.wit.activities.auth.AuthManager
import org.wit.activities.databinding.ActivityAthleteListBinding
import org.wit.activities.helpers.ThemeHelper
import org.wit.activities.main.MainApp
import org.wit.activities.models.AthleteModel

class AthleteListActivity : AppCompatActivity(), AthleteListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityAthleteListBinding
    private lateinit var adapter: AthleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAthleteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        app = application as MainApp

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AthleteAdapter(emptyList(), this)
        binding.recyclerView.adapter = adapter
        refreshList()


        binding.fabAddAthlete.setOnClickListener {
            getResult.launch(Intent(this, AthleteActivity::class.java))
        }

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                val athlete = adapter.getItem(pos)

                app.athletes.delete(athlete) { success ->
                    runOnUiThread {
                        if (success) {
                            Snackbar.make(binding.root, "Deleted ${athlete.name}", Snackbar.LENGTH_LONG)
                                .setAction("UNDO") {
                                    val username = AuthManager.getUsername(this@AthleteListActivity) ?: ""
                                    val undoAthlete = athlete.copy(
                                        id = "",
                                        ownerUsername = username
                                    )
                                    app.athletes.create(undoAthlete) { _ ->
                                        refreshList()
                                    }
                                }
                                .show()
                        } else {
                            Snackbar.make(binding.root, "Delete failed", Snackbar.LENGTH_LONG).show()
                        }

                        refreshList()
                    }
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val username = AuthManager.getUsername(this) ?: ""

        app.athletes.findAll { allAthletes ->
            val mine = allAthletes.filter { it.ownerUsername == username }

            runOnUiThread {
                adapter.setData(mine)
                toggleEmptyState(mine)
            }
        }
    }

    private fun toggleEmptyState(list: List<AthleteModel>) {
        val empty = list.isEmpty()
        binding.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (empty) View.GONE else View.VISIBLE
    }


    override fun onAthleteClick(athlete: AthleteModel) {
        val intent = Intent(this, AthleteActivity::class.java)
        intent.putExtra("athlete_edit", athlete)
        getResult.launch(intent)

    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) refreshList()
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.theme_system -> { ThemeHelper.setThemeMode(this, 0); recreate(); true }
            R.id.theme_light  -> { ThemeHelper.setThemeMode(this, 1); recreate(); true }
            R.id.theme_dark   -> { ThemeHelper.setThemeMode(this, 2); recreate(); true }

            R.id.item_add -> {
                getResult.launch(Intent(this, AthleteActivity::class.java))
                true
            }
            R.id.item_cancel -> true
            R.id.item_logout -> {
                AuthManager.logout(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
