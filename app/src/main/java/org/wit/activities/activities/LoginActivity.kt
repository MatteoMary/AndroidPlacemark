package org.wit.activities.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.databinding.ActivityLoginBinding
import org.wit.activities.auth.AuthManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.wit.activities.helpers.ThemeHelper


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        ThemeHelper.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (AuthManager.isLoggedIn(this)) {
            goToAthleteList()
            return
        }

        if (!AuthManager.isRegistered(this)) {
        }

        binding.buttonLogin.setOnClickListener { view ->
            val username = binding.editTextUsername.text?.toString()?.trim().orEmpty()
            val password = binding.editTextPassword.text?.toString()?.trim().orEmpty()

            if (username.isEmpty() || password.isEmpty()) {
                Snackbar.make(view, "Please enter username and password", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!AuthManager.isRegistered(this)) {
                Snackbar.make(view, "No account found, please sign up first", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val success = AuthManager.login(this, username, password)
            if (success) {
                goToAthleteList()
            } else {
                Snackbar.make(view, "Invalid credentials", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.buttonGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun goToAthleteList() {
        startActivity(Intent(this, AthleteListActivity::class.java))
        finish()
    }
}
