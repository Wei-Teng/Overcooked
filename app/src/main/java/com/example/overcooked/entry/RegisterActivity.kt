package com.example.overcooked.entry

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.overcooked.R
import com.example.overcooked.data.model.User
import com.example.overcooked.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firestoreDb = Firebase.firestore
        setupTvLoginNowListener()
        setupIbBackArrowListener()
        setupBtRegisterListener()
    }

    private fun setupTvLoginNowListener() {
        binding.tvLoginNow.setOnClickListener {
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    private fun setupIbBackArrowListener() {
        binding.ibBackArrow.setOnClickListener {
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    private fun setupBtRegisterListener() {
        binding.btRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val region = binding.etRegion.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val cfmPassword = binding.etConfirmPassword.text.toString().trim()
            if (isValidUsername(username) && isValidEmail(email)
                && isValidRegion(region) && isValidPassword(password)
                && isValidConfirmPassword(password, cfmPassword)
            ) {
                binding.pbLoading.visibility = View.VISIBLE
                binding.btRegister.visibility = View.INVISIBLE
                val user = User(email, password, region, username)
                createAndStoreUser(user)
                clearCredentials()
            }
        }
    }

    private fun isValidUsername(name: String): Boolean {
        return if (name.length >= 2)
            true
        else {
            binding.etUsername.error = "At Least 2 Character Long"
            binding.etUsername.requestFocus()
            false
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

    private fun isValidRegion(region: String): Boolean {
        val countryCodes = Locale.getISOCountries()
        val targetName =
            if (region.length >= 2)
                region.substring(0, 1).uppercase() + region.substring(1).lowercase()
            else
                ""
        for (countryCode in countryCodes) {
            val locale = Locale("en", countryCode)
            if (locale.displayCountry == targetName)
                return true
        }
        binding.etRegion.error = "Invalid Region"
        binding.etRegion.requestFocus()
        return false
    }

    private fun isValidPassword(password: String): Boolean {
        return if (password.length >= 6)
            true
        else {
            binding.etPassword.error = "At Least 6 Characters Long"
            binding.etPassword.requestFocus()
            false
        }
    }

    private fun isValidConfirmPassword(password: String, cfmPassword: String): Boolean {
        return if (password == cfmPassword)
            true
        else {
            binding.etConfirmPassword.error = "Must Same With Password"
            binding.etConfirmPassword.requestFocus()
            false
        }
    }

    private fun createAndStoreUser(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                user.firebaseAuthId = FirebaseAuth.getInstance().currentUser!!.uid
                firestoreDb.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Please verify your email address",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateTo(VerifyEmailActivity::class.java)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                binding.pbLoading.visibility = View.INVISIBLE
                binding.btRegister.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = View.INVISIBLE
                binding.btRegister.visibility = View.VISIBLE
            }
    }

    private fun navigateTo(destinationClassName: Class<*>) {
        Thread {
            intent = Intent(this, destinationClassName)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }.start()
    }

    private fun clearCredentials() {
        binding.etUsername.text.clear()
        binding.etEmail.text.clear()
        binding.etRegion.text.clear()
        binding.etPassword.text.clear()
        binding.etConfirmPassword.text.clear()
    }
}
