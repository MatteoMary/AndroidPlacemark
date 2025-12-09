package org.wit.activities.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.activities.databinding.ActivitySignupBinding
import org.wit.activities.auth.AuthManager

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignup.setOnClickListener { view ->
            val username = binding.editTextUsername.text?.toString()?.trim().orEmpty()
            val password = binding.editTextPassword.text?.toString()?.trim().orEmpty()
            val confirm = binding.editTextConfirmPassword.text?.toString()?.trim().orEmpty()

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Snackbar.make(view, "Passwords do not match", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AuthManager.register(this, username, password)
            Snackbar.make(view, "Account created. Please log in.", Snackbar.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
