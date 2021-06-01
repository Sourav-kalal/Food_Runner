package com.example.food_order_app.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.food_order_app.R

class OrderPlaced : AppCompatActivity() {

    lateinit var tv_res_name: TextView
    lateinit var tv_total: TextView
    lateinit var btn_back_home: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var relativeLayout: RelativeLayout
    lateinit var img_success: ImageView
    lateinit var tv_placed: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        img_success = findViewById(R.id.img_success_check)
        tv_placed = findViewById(R.id.tv_order_placed)
        tv_res_name = findViewById(R.id.tv_restaurant_name_order)
        tv_total = findViewById(R.id.tv_total)
        btn_back_home = findViewById(R.id.btn_back_home)

        sharedPreferences =
            getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)

        tv_res_name.text = sharedPreferences.getString("restaurant_name", "")
        tv_total.text = String.format(getString(R.string.total), intent.getStringExtra("total"))
        tv_placed.text = getString(R.string.order_placed)
        btn_back_home.setOnClickListener {
            val intent = Intent(this@OrderPlaced, Dashboard::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@OrderPlaced, Dashboard::class.java))
        finishAffinity()
    }
}