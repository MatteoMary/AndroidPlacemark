package org.wit.activities.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.R
import org.wit.activities.databinding.ActivityAthleteBinding
import org.wit.activities.main.MainApp
import org.wit.activities.models.AthleteModel
import timber.log.Timber.i

class AthleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAthleteBinding
    var athlete = AthleteModel()
    lateinit var app: MainApp

    var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAthleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        if (intent.hasExtra("athlete_edit")) {
            isEdit = true
            athlete = intent.extras?.getParcelable("athlete_edit")!!
            binding.athleteName.setText(athlete.title)
            binding.athleteNotes.setText(athlete.description)
            binding.btnAdd.text = getString(R.string.button_saveAthlete)
        } else {
            binding.btnAdd.text = getString(R.string.button_addAthlete)
        }


        binding.btnAdd.setOnClickListener {
            athlete.title = binding.athleteName.text.toString()
            athlete.description = binding.athleteNotes.text.toString()

            if (athlete.title.isEmpty()) {
                Snackbar.make(it, getString(R.string.err_enter_name), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
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
        when (item.itemId) {
            R.id.item_cancel -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
