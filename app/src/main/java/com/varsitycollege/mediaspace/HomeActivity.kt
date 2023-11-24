package com.varsitycollege.mediaspace


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.varsitycollege.mediaspace.data.HomeViewModel
import com.varsitycollege.mediaspace.databinding.ActivityHomeBinding
import com.varsitycollege.mediaspace.ui.CartFragment
import com.varsitycollege.mediaspace.ui.CategoriesFragment
import com.varsitycollege.mediaspace.ui.ProfileFragment
import com.varsitycollege.mediaspace.ui.TrendingFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var model: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize viewmodel and bottom nav view
        model = ViewModelProvider(this)[HomeViewModel::class.java]
        bottomNavigationView = binding.bottomNavView

        //Set startup fragment, keep current fragment if dark mode changes
        if (model.currentFragment.value != null) {
            replaceFragment(model.currentFragment.value!!)
        } else {
            replaceFragment(TrendingFragment())
        }

        //Handle bottom nav view press
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.bottom_trending -> {
                    replaceFragment(TrendingFragment())
                    model.currentFragment.value = TrendingFragment()
                    true
                }
                R.id.bottom_category -> {
                    replaceFragment(CategoriesFragment())
                    model.currentFragment.value = CategoriesFragment()
                    true
                }
                R.id.bottom_cart -> {
                    replaceFragment(CartFragment())
                    model.currentFragment.value = CartFragment()
                    true
                }
                R.id.bottom_profile -> {
                    replaceFragment(ProfileFragment())
                    model.currentFragment.value = ProfileFragment()
                    true
                }
                else -> false
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}