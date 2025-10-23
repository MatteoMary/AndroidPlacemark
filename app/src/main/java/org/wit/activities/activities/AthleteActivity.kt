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
    var athlete = AthleteModel()
    lateinit var app: MainApp

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

        if (intent.hasExtra("athlete_edit")) {
            isEdit = true
            athlete = intent.extras?.getParcelable("athlete_edit")!!
            binding.athleteName.setText(athlete.title)
            binding.athleteNotes.setText(athlete.description)
            binding.roleDropdown.setText(athlete.role, false)
            binding.btnAdd.text = getString(R.string.button_saveAthlete)
        } else {
            binding.btnAdd.text = getString(R.string.button_addAthlete)
        }

        binding.btnAdd.setOnClickListener {
            athlete.title = binding.athleteName.text.toString()
            athlete.description = binding.athleteNotes.text.toString()
            athlete.role = binding.roleDropdown.text.toString()

            if (athlete.title.isEmpty()) {
                Snackbar.make(it, getString(R.string.err_enter_name), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (isEdit) app.athletes.update(athlete) else app.athletes.create(athlete)
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.item_delete)?.isVisible = isEdit
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_delete -> {
                app.athletes.delete(athlete)
                setResult(RESULT_OK); finish(); true
            }
            R.id.item_cancel -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
