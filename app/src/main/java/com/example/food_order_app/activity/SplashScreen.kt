package com.example.food_order_app.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.food_order_app.R


class SplashScreen : AppCompatActivity() {

    lateinit var app_name : TextView
    lateinit var anime : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.primaryLightColor))
        }

        app_name = findViewById(R.id.app_name)
        anime = findViewById(R.id.img_logo)

        app_name.animate().translationY(1400F).setDuration(1000).setStartDelay(3000)
        anime.animate().translationY(1400F).setDuration(1000).setStartDelay(3000)

        Handler().postDelayed({
            val intent = Intent(this@SplashScreen, LoginPage::class.java)
            startActivity(intent)
            finish()
        },4000)
    }
}