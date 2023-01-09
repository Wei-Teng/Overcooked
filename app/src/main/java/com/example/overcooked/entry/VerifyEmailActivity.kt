package com.example.overcooked.entry

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.overcooked.databinding.ActivityVerifyEmailBinding
import com.google.firebase.auth.FirebaseAuth

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        sendVerificationEmail()
        setupBtContinueToLoginListener()
        setupBtResendVerificationEmail()
    }

    private fun setupBtContinueToLoginListener() {
        binding.btContinueToLogin.setOnClickListener {
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    private fun setupBtResendVerificationEmail() {
        binding.btResendVerificationEmail.setOnClickListener {
            sendVerificationEmail()
        }
    }

    private fun sendVerificationEmail() {
        firebaseAuth.currentUser!!.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(this, "Verification Email has been sent", Toast.LENGTH_SHORT)
                    .show()
                disableBtForOneMinute(binding.btResendVerificationEmail, binding.tvWarning)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun disableBtForOneMinute(button : Button, textDisplay : TextView) {
        button.isEnabled = false
        textDisplay.visibility = TextView.VISIBLE
        Handler(mainLooper).postDelayed({
            button.isEnabled = true
            textDisplay.visibility = TextView.INVISIBLE
        }, 60000)
    }
}