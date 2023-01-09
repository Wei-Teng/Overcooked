package com.example.overcooked.entry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.overcooked.R
import com.example.overcooked.databinding.ActivityLoginBinding
import com.example.overcooked.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        setupTvRegisterNowListener()
        setupTvForgetPasswordListener()
        setupBtLoginListener()
    }

    private fun setupTvRegisterNowListener() {
        binding.tvRegisterNow.setOnClickListener {
            navigateTo(RegisterActivity::class.java)
        }
    }

    private fun setupTvForgetPasswordListener() {
        binding.tvForgetPassword.setOnClickListener {
            navigateTo(ForgetPasswordActivity::class.java)
        }
    }

    private fun setupBtLoginListener() {
        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (isValidEmail(email) && isValidPassword(password)) {
                binding.pbLoading.visibility = View.VISIBLE
                binding.btLogin.visibility = View.INVISIBLE
                tryToSignIn(email, password)
                binding.etEmail.text.clear()
                binding.etPassword.text.clear()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).find())
            true
        else {
            binding.etEmail.error = "Email Format Is Incorrect!"
            binding.etEmail.requestFocus()
            false
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return if (password.isNotEmpty())
            true
        else {
            binding.etPassword.error = "Password Field Cannot Be Empty!"
            binding.etPassword.requestFocus()
            false
        }
    }

    private fun tryToSignIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            lifecycleScope.launch {
                if (firebaseAuth.currentUser!!.isEmailVerified) {
                    val firebaseAuthId =
                        FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)
                            .get().await().get("firebaseAuthId")?.toString()
                    if (firebaseAuthId == firebaseAuth.currentUser!!.uid) {
                        navigateTo(MainActivity::class.java)
                        finish()
                    } else
                        Toast.makeText(
                            this@LoginActivity,
                            "User account have been removed",
                            Toast.LENGTH_SHORT
                        ).show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please verify your email first",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateTo(VerifyEmailActivity::class.java)
                }
                binding.pbLoading.visibility = View.INVISIBLE
                binding.btLogin.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(this, "The email and/or password is invalid", Toast.LENGTH_SHORT).show()
            binding.pbLoading.visibility = View.INVISIBLE
            binding.btLogin.visibility = View.VISIBLE
        }
    }

    private fun navigateTo(destinationClassName: Class<*>) {
        Thread {
            intent = Intent(this, destinationClassName)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }.start()
    }
}