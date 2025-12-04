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
import org.wit.activities.models.Country

class AthleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAthleteBinding
    private var athlete = AthleteModel()
    private lateinit var app: MainApp
    private var isEdit = false

    private val countryList = Country.values().toList()
    private val countryDisplayNames = countryList.map { it.displayName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAthleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        val roles = listOf("Sprinter", "Distance", "All-rounder", "Field Event")
        val groups = listOf("U16", "U18", "U20", "U23", "Senior", "Masters")

        binding.roleDropdown.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        )
        binding.groupDropdown.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, groups)
        )
        binding.countryDropdown.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countryDisplayNames)
        )

        binding.roleDropdown.setOnItemClickListener { _, _, pos, _ ->
            athlete.role = roles[pos]
        }
        binding.groupDropdown.setOnItemClickListener { _, _, pos, _ ->
            athlete.group = groups[pos]
        }
        binding.countryDropdown.setOnItemClickListener { _, _, pos, _ ->
            athlete.country = countryList[pos]
        }

        if (intent.hasExtra("athlete_edit")) {
            isEdit = true
            athlete = intent.extras?.getParcelable("athlete_edit")!!

            binding.athleteName.setText(athlete.name)
            binding.athleteNotes.setText(athlete.description)
            binding.roleDropdown.setText(athlete.role, false)
            binding.groupDropdown.setText(athlete.group, false)

            binding.athletePB.setText(formatSecondsToTime(athlete.personalBestSeconds))

            binding.countryDropdown.setText(athlete.country.displayName, false)

            binding.switchActive.isChecked = athlete.isActive
            binding.btnAdd.text = getString(R.string.button_saveAthlete)
        } else {
            binding.btnAdd.text = getString(R.string.button_addAthlete)
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.athleteName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                Snackbar.make(it, getString(R.string.err_enter_name), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            athlete.name = name
            athlete.description = binding.athleteNotes.text?.toString()?.trim().orEmpty()

            // Parse PB input to seconds
            val pbInput = binding.athletePB.text?.toString()?.trim().orEmpty()
            athlete.personalBestSeconds = parsePersonalBestToSeconds(pbInput)

            val pickedRole = binding.roleDropdown.text?.toString()?.trim().orEmpty()
            val pickedGroup = binding.groupDropdown.text?.toString()?.trim().orEmpty()
            val pickedCountryName = binding.countryDropdown.text?.toString()?.trim().orEmpty()

            if (pickedRole.isNotEmpty()) athlete.role = pickedRole
            if (pickedGroup.isNotEmpty()) athlete.group = pickedGroup

            if (pickedCountryName.isNotEmpty()) {
                val selectedCountry = countryList.firstOrNull {
                    it.displayName == pickedCountryName
                }
                if (selectedCountry != null) {
                    athlete.country = selectedCountry
                }
            }

            athlete.isActive = binding.switchActive.isChecked

            if (isEdit) {
                app.athletes.update(athlete)
            } else {
                app.athletes.create(athlete)
            }

            setResult(RESULT_OK)
            finish()
        }
    }
    private fun parsePersonalBestToSeconds(input: String): Int? {
        if (input.isBlank()) return null

        // Support "mm:ss" (e.g., "3:45")
        val parts = input.split(":")
        return if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull()
            val seconds = parts[1].toIntOrNull()
            if (minutes != null && seconds != null) {
                minutes * 60 + seconds
            } else {
                null
            }
        } else {
            // plain seconds (e.g., "225")
            input.toIntOrNull()
        }
    }

    private fun formatSecondsToTime(totalSeconds: Int?): String {
        if (totalSeconds == null) return ""
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_cancel) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
