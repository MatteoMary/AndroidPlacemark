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
        val groups = listOf("U16", "U18", "U20", "U23", "Senior", "Masters")
        val countries = listOf("Ireland", "United Kingdom", "USA", "France", "Germany", "Spain", "Italy")

        binding.roleDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles))
        binding.groupDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, groups))
        binding.countryDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countries))

        binding.roleDropdown.setOnItemClickListener { _, _, pos, _ -> athlete.role = roles[pos] }
        binding.groupDropdown.setOnItemClickListener { _, _, pos, _ -> athlete.group = groups[pos] }
        binding.countryDropdown.setOnItemClickListener { _, _, pos, _ -> athlete.country = countries[pos] }

        if (intent.hasExtra("athlete_edit")) {
            isEdit = true
            athlete = intent.extras?.getParcelable("athlete_edit")!!
            binding.athleteName.setText(athlete.name)
            binding.athleteNotes.setText(athlete.description)
            binding.roleDropdown.setText(athlete.role, false)
            binding.groupDropdown.setText(athlete.group, false)
            binding.athletePB.setText(athlete.personalBest)
            binding.countryDropdown.setText(athlete.country, false)
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
            var ok = true

            val pb = binding.athletePB.text?.toString()?.trim().orEmpty()
            val pbOk = pb.isEmpty() || Regex(
                """^(?:\d{1,2}:[0-5]\d:[0-5]\d|[0-5]?\d:[0-5]\d)(?:\s+\d{3,4}m)?$""").matches(pb)
            if (!pbOk) {
                binding.athletePBLayout.error = "Use hh:mm:ss or mm:ss (optional distance)"
                ok = false
            } else binding.athletePBLayout.error = null

            if (!ok) return@setOnClickListener

            athlete.name = name
            athlete.description = binding.athleteNotes.text?.toString()?.trim().orEmpty()
            athlete.personalBest = binding.athletePB.text?.toString()?.trim().orEmpty()

            val pickedRole = binding.roleDropdown.text?.toString()?.trim().orEmpty()
            val pickedGroup = binding.groupDropdown.text?.toString()?.trim().orEmpty()
            val pickedCountry = binding.countryDropdown.text?.toString()?.trim().orEmpty()

            athlete.role = if (pickedRole.isNotEmpty()) pickedRole else athlete.role
            athlete.group = if (pickedGroup.isNotEmpty()) pickedGroup else athlete.group
            athlete.country = if (pickedCountry.isNotEmpty()) pickedCountry else athlete.country
            athlete.isActive = binding.switchActive.isChecked

            if (isEdit) app.athletes.update(athlete) else app.athletes.create(athlete)
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_cancel) {
            finish(); return true
        }
        return super.onOptionsItemSelected(item)
    }
}
