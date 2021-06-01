package com.example.food_order_app.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.food_order_app.R

class Profile : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var userName: TextView
    lateinit var mobileNumber: TextView
    lateinit var email: TextView
    lateinit var address: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        sharedPreferences =
            activity?.getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)!!

        userName = view.findViewById(R.id.username)
        mobileNumber = view.findViewById(R.id.phoneNumber)
        email = view.findViewById(R.id.user_email)
        address = view.findViewById(R.id.user_address)

        userName.text = sharedPreferences.getString("name", "")
        mobileNumber.text = sharedPreferences.getString("mobile_number", "")
        email.text = sharedPreferences.getString("email", "")
        address.text = sharedPreferences.getString("address", "")

        return view
    }
}