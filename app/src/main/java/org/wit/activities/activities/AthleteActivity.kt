package org.wit.activities.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.R
import org.wit.activities.databinding.ActivityAthleteBinding
import org.wit.activities.main.MainApp
import org.wit.activities.models.AthleteModel

class AthleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAthleteBinding
    private var athlete = AthleteModel()
    private lateinit var app: MainApp
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAthleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        val roles = listOf("Sprinter", "Distance", "All-rounder", "Field Event")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.roleDropdown.setAdapter(roleAdapter)

        binding.roleDropdown.setOnItemClickListener { _, _, pos, _ ->
            athlete.role = roles[pos]
        }

        if (intent.hasExtra("athlete_edit")) {
            isEdit = true
            athlete = intent.extras?.getParcelable("athlete_edit")!!
            binding.athleteName.setText(athlete.title)
            binding.athleteNotes.setText(athlete.description)
            binding.roleDropdown.setText(athlete.role, false)
            binding.btnAdd.text = getString(R.string.button_saveAthlete)
        } else {
            athlete.role = ""
            binding.btnAdd.text = getString(R.string.button_addAthlete)
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.athleteName.text?.toString()?.trim().orEmpty()
            val notes = binding.athleteNotes.text?.toString()?.trim().orEmpty()
            val pickedRole = binding.roleDropdown.text?.toString()?.trim().orEmpty()

            if (name.isEmpty()) {
                Snackbar.make(it, getString(R.string.err_enter_name), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            athlete.title = name
            athlete.description = notes

            athlete.role = when {
                pickedRole.isNotEmpty() -> pickedRole
                athlete.role.isNotEmpty() -> athlete.role
                else -> "All-rounder"
            }

            if (isEdit) {
                app.athletes.update(athlete)
            } else {
                app.athletes.create(athlete)
            }
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_cancel) finish()
        return super.onOptionsItemSelected(item)
    }
}
