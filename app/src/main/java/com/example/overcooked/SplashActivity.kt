package com.example.overcooked

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.overcooked.entry.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        progressBar = findViewById(R.id.progressBar)
        firebaseAuth = FirebaseAuth.getInstance()
        navigateToSuitableActivity()
    }

    private fun navigateToSuitableActivity() {
        Thread {
            runProgressBarAnimation()
            Handler(mainLooper).post {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }
        }.start()
    }

    private fun runProgressBarAnimation() {
        var currentProgress = 0
        while (currentProgress < 100) {
            currentProgress += 2
            // Code below need to be replaced by actual process in the future
            android.os.SystemClock.sleep(10)
            progressBar.progress = currentProgress
        }
    }
}