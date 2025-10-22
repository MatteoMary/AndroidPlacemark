package org.wit.activities.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.databinding.ActivityAthleteBinding
import org.wit.activities.main.MainApp
import org.wit.activities.models.AthleteModel
import timber.log.Timber.i

class AthleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAthleteBinding
    var athlete = AthleteModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAthleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        i("Athlete Activity started...")

        binding.btnAdd.setOnClickListener {
            athlete.title = binding.athleteName.text.toString()
            athlete.description = binding.athleteNotes.text.toString()
            if (athlete.title.isNotEmpty()) {

                app.athletes.add(athlete.copy())
                i("add Button Pressed: $athlete")
                for (index in app.athletes.indices) {
                    i("Athlete[$index]:${this.app.athletes[index]}")
                }
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
