package com.example.overcooked.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.overcooked.R
import com.example.overcooked.databinding.ActivityMainBinding
import com.example.overcooked.ui.addrecipe.AddRecipeFragment
import com.example.overcooked.ui.home.HomeFragment
import com.example.overcooked.ui.myprofile.MyProfileFragment
import com.example.overcooked.ui.savedrecipe.SavedRecipeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, HomeFragment()).commit()
        setupBottomNavView()
    }

    private fun setupBottomNavView() {
        val bottomNavView = binding.bottomNavView
        bottomNavView.setItemSelected(R.id.bottomNavView, true)
        bottomNavView.setOnItemSelectedListener {
            var fragment : Fragment? = null
            when (it) {
                R.id.homeFragment -> fragment = HomeFragment()
                R.id.savedRecipeFragment -> fragment = SavedRecipeFragment()
                R.id.addRecipeFragment -> fragment = AddRecipeFragment()
                R.id.notificationFragment -> fragment = NotificationFragment()
                R.id.myProfileFragment -> fragment = MyProfileFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment!!).commit()
        }
    }
}