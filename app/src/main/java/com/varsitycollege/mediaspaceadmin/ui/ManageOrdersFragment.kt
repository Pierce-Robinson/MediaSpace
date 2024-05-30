package com.varsitycollege.mediaspaceadmin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.database.FirebaseDatabase
import com.varsitycollege.mediaspaceadmin.BuildConfig
import com.varsitycollege.mediaspaceadmin.R
import com.varsitycollege.mediaspaceadmin.data.Order
import com.varsitycollege.mediaspaceadmin.data.OrderAdapter
import com.varsitycollege.mediaspaceadmin.data.User
import com.varsitycollege.mediaspaceadmin.databinding.FragmentDashboardBinding
import com.varsitycollege.mediaspaceadmin.databinding.FragmentManageOrdersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManageOrdersFragment : Fragment() {

    private var _binding: FragmentManageOrdersBinding? = null
    private var orders = arrayListOf<Order>()
    private lateinit var database: FirebaseDatabase

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageOrdersBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)

        // custom item adapter
        val items = arrayOf("Pending", "Paid", "Shipped", "Delivered")
        var autoCompleteTextView = binding.autoCompleteTextView as MaterialAutoCompleteTextView
        val adapter = ArrayAdapter(requireContext(), R.layout.layout_list_item, items)
        autoCompleteTextView.setAdapter(adapter)
        //set initial text
        //https://stackoverflow.com/questions/44963164/autocompletetextview-item-selection-programmatically
        //accessed 27 November 2023
        autoCompleteTextView.setText("Pending", false)

        //on item clicked listener for autoCompleteTextView
        //https://copyprogramming.com/howto/how-to-add-listener-to-autocompletetextview-android
        //accessed 27 November 2023
        autoCompleteTextView.setOnItemClickListener { adapterView, view, position, l ->
            val value = adapter.getItem(position) ?: "Pending"
            getOrders(value)
        }

        //Layout manager
        binding.manageOrderRecycler.layoutManager = LinearLayoutManager(this@ManageOrdersFragment.context)

        //Get user orders
        getOrders("Pending")

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getOrders(status: String) {
        val userRef = database.getReference("users")
        orders.clear()
        //Get user data
        userRef.get()
            .addOnSuccessListener {
                for (u in it.children) {
                    val user = u.getValue(User::class.java)
                    if (user != null) {
                        //Get orders
                        if (user.orderHistory != null) {
                            for (o in user.orderHistory!!) {
                                //Add orders for selected status
                                if (o.status == status) {
                                    orders.add(o)
                                }
                            }
                        }

                    }
                    else {
                        Log.e("Error","Failed to find user.")
                    }
                }
                //Update ui and adapter
                val adapter = OrderAdapter(orders)
                if (_binding != null) {
                    binding.progressBar.visibility = GONE
                    binding.menu.visibility = VISIBLE
                    binding.solidLine1.visibility = VISIBLE
                    if (orders.isNotEmpty()) {
                        binding.manageOrderRecycler.adapter = adapter
                        binding.manageOrderRecycler.visibility = VISIBLE
                        binding.noOrders.visibility = GONE
                    }
                    else {
                        binding.manageOrderRecycler.visibility = GONE
                        binding.noOrders.visibility = VISIBLE
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Error","Failed to get user order data.")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
