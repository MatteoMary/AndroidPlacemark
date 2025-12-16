package org.wit.activities.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.R
import org.wit.activities.auth.AuthManager
import org.wit.activities.databinding.ActivityAthleteBinding
import org.wit.activities.main.MainApp
import org.wit.activities.models.AthleteModel
import org.wit.activities.models.Country
import com.google.android.material.datepicker.MaterialDatePicker
import org.wit.activities.helpers.ThemeHelper


class AthleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAthleteBinding
    private var athlete = AthleteModel()
    private lateinit var app: MainApp
    private var isEdit = false

    private val countryList = Country.values().toList()
    private val countryDisplayNames = countryList.map { it.displayName }

    private val events = listOf(
        "100m", "200m", "400m",
        "800m", "1500m", "3000m", "5000m", "10000m",
        "Half Marathon", "Marathon"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applySavedTheme(this)
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
        binding.eventDropdown.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, events)
        )

        binding.dobText.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Next Race")
                .build()

            picker.addOnPositiveButtonClickListener { millis ->
                athlete.dateOfBirth = millis
                binding.dobText.setText(android.text.format.DateFormat.format("dd/MM/yyyy", millis))
            }

            picker.show(supportFragmentManager, "DOB_PICKER")
        }

        binding.roleDropdown.setOnItemClickListener { _, _, pos, _ -> athlete.role = roles[pos] }
        binding.groupDropdown.setOnItemClickListener { _, _, pos, _ -> athlete.group = groups[pos] }
        binding.countryDropdown.setOnItemClickListener { _, _, pos, _ ->
            athlete.country = countryList[pos]
        }
        binding.eventDropdown.setOnItemClickListener { _, _, pos, _ ->
            val ev = events[pos]
            athlete.event = ev
            updatePbHint(ev)
        }

        if (intent.hasExtra("athlete_edit")) {
            isEdit = true
            athlete = intent.extras?.getParcelable("athlete_edit")!!

            binding.athleteName.setText(athlete.name)
            binding.athleteNotes.setText(athlete.description)
            binding.roleDropdown.setText(athlete.role, false)
            binding.groupDropdown.setText(athlete.group, false)
            binding.countryDropdown.setText(athlete.country.displayName, false)

            athlete.dateOfBirth?.let { dob ->
                binding.dobText.setText(formatDob(dob))
            }

            if (athlete.event.isNotEmpty()) {
                binding.eventDropdown.setText(athlete.event, false)
                updatePbHint(athlete.event)
            }

            binding.athletePB.setText(formatSecondsToTimeForEdit(athlete))
            binding.switchActive.isChecked = athlete.isActive
            binding.btnAdd.text = getString(R.string.button_saveAthlete)
        } else {
            binding.btnAdd.text = getString(R.string.button_addAthlete)
        }

        binding.btnAdd.setOnClickListener { view ->
            val name = binding.athleteName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                Snackbar.make(view, getString(R.string.err_enter_name), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            athlete.name = name
            athlete.description = binding.athleteNotes.text?.toString()?.trim().orEmpty()

            val eventText = binding.eventDropdown.text?.toString()?.trim().orEmpty()
            athlete.event = eventText

            val pbInput = binding.athletePB.text?.toString()?.trim().orEmpty()
            if (pbInput.isNotEmpty()) {
                val parsed = parsePB(pbInput, eventText)
                if (parsed == null) {
                    binding.athletePBLayout.error =
                        "Invalid PB format for $eventText. Try 10.45 or 3:45 or 2:10:30."
                    return@setOnClickListener
                } else {
                    binding.athletePBLayout.error = null
                    athlete.personalBestSeconds = parsed
                }
            } else {
                athlete.personalBestSeconds = null
                binding.athletePBLayout.error = null
            }

            val pickedCountryName = binding.countryDropdown.text?.toString()?.trim().orEmpty()
            if (pickedCountryName.isNotEmpty()) {
                countryList.firstOrNull { it.displayName == pickedCountryName }?.let {
                    athlete.country = it
                }
            }

            athlete.isActive = binding.switchActive.isChecked

            if (!isEdit) {
                athlete.ownerUsername = AuthManager.getUsername(this) ?: ""
            }

            if (isEdit) {
                app.athletes.update(athlete) {

                }
            } else {
                app.athletes.create(athlete) {
                }
                setResult(RESULT_OK)
                finish()
            }

                binding.btnAdd.isEnabled = false

            val doneOk: (Boolean) -> Unit = { success ->
                runOnUiThread {
                    binding.btnAdd.isEnabled = true

                    if (success) {
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Save failed (check Firestore rules/internet)",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            if (isEdit) {
                app.athletes.update(athlete, doneOk)
            } else {
                app.athletes.create(athlete, doneOk)
            }
        }
    }


        private fun updatePbHint(event: String) {
        binding.athletePBLayout.hint = when (event) {
            "100m", "200m", "400m" -> "Personal Best (ss.ms)"
            "800m", "1500m", "3000m", "5000m", "10000m" -> "Personal Best (mm:ss)"
            "Half Marathon", "Marathon" -> "Personal Best (hh:mm:ss)"
            else -> "Personal Best"
        }
    }

    private fun parsePB(input: String, event: String): Int? {
        if (input.isBlank()) return null
        val trimmed = input.trim()

        if (event in listOf("100m", "200m", "400m")) {
            val seconds = trimmed.toFloatOrNull() ?: return null
            return (seconds * 100).toInt()
        }

        val parts = trimmed.split(":")
        return when (parts.size) {
            2 -> {
                val minutes = parts[0].toIntOrNull() ?: return null
                val secs = parts[1].toFloatOrNull() ?: return null
                (minutes * 60 + secs).toInt()
            }
            3 -> {
                val h = parts[0].toIntOrNull() ?: return null
                val m = parts[1].toIntOrNull() ?: return null
                val s = parts[2].toIntOrNull() ?: return null
                h * 3600 + m * 60 + s
            }
            else -> trimmed.toIntOrNull()
        }
    }

    private fun formatDob(millis: Long): String =
        android.text.format.DateFormat.format("dd/MM/yyyy", millis).toString()


    private fun formatSecondsToTimeForEdit(athlete: AthleteModel): String {
        val pb = athlete.personalBestSeconds ?: return ""
        val event = athlete.event

        return when (event) {
            "100m", "200m", "400m" -> {
                val seconds = pb / 100f
                "%.2f".format(seconds)
            }
            "Half Marathon", "Marathon" -> {
                val h = pb / 3600
                val m = (pb % 3600) / 60
                val s = pb % 60
                "%02d:%02d:%02d".format(h, m, s)
            }
            else -> {
                val minutes = pb / 60
                val seconds = pb % 60
                "%d:%02d".format(minutes, seconds)
            }
        }
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
