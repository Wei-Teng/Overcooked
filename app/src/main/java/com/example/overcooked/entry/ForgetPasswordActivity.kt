package com.example.overcooked.entry

import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.overcooked.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        setupIbBackArrowListener()
        setupTvLoginNowListener()
        setupBtSendPasswordResetEmailListener()
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    private fun setupTvLoginNowListener() {
        binding.tvLoginNow.setOnClickListener {
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    private fun setupBtSendPasswordResetEmailListener() {
        binding.btSendPasswordResetEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (Patterns.EMAIL_ADDRESS.matcher(email).find())
                tryToSendPasswordResetEmail(email)
            else {
                binding.etEmail.error = "Email Format Is Incorrect!"
                binding.etEmail.requestFocus()
            }
        }
    }

    private fun tryToSendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Check your email to reset your password",
                    Toast.LENGTH_SHORT
                ).show()
                disableBtForOneMinute(binding.btSendPasswordResetEmail, binding.tvWarning)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun disableBtForOneMinute(button: Button, textDisplay: TextView) {
        button.isEnabled = false
        textDisplay.visibility = TextView.VISIBLE
        Handler(mainLooper).postDelayed({
            button.isEnabled = true
            textDisplay.visibility = TextView.INVISIBLE
        }, 60000)
    }
}