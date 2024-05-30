package com.varsitycollege.mediaspaceadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.varsitycollege.mediaspaceadmin.data.HomeViewModel
import com.varsitycollege.mediaspaceadmin.databinding.ActivityHomeBinding
import com.varsitycollege.mediaspaceadmin.ui.AddProductsFragment
import com.varsitycollege.mediaspaceadmin.ui.DashboardFragment
import com.varsitycollege.mediaspaceadmin.ui.ManageOrdersFragment
import com.varsitycollege.mediaspaceadmin.ui.ManageProductsFragment

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
            replaceFragment(DashboardFragment())
        }

        //Handle bottom nav view press
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.bottom_dashboard -> {
                    replaceFragment(DashboardFragment())
                    model.currentFragment.value = DashboardFragment()
                    true
                }
                R.id.bottom_add_products -> {
                    replaceFragment(AddProductsFragment())
                    model.currentFragment.value = AddProductsFragment()
                    true
                }
                R.id.bottom_manage_products -> {
                    replaceFragment(ManageProductsFragment())
                    model.currentFragment.value = ManageProductsFragment()
                    true
                }
                R.id.bottom_manage_orders -> {
                    replaceFragment(ManageOrdersFragment())
                    model.currentFragment.value = ManageOrdersFragment()
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