package com.example.food_order_app.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.food_order_app.R
import com.example.food_order_app.fragment.*
import com.google.android.material.navigation.NavigationView

class Dashboard : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPref),Context.MODE_PRIVATE)

        var previousMenuItem : MenuItem? = null

        drawerLayout = findViewById(R.id.Drawer_layout)
        toolbar = findViewById(R.id.dash_tool_bar)
        frameLayout = findViewById(R.id.frame_layout)
        navigationView = findViewById(R.id.navigation_view)


        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Restaurants"



        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@Dashboard,
            drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        )

        val username = sharedPreferences.getString("name","")
        val mobile = sharedPreferences.getString("mobile_number","")

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        inflateFragment(Restaurants())

        val header = navigationView.getHeaderView(0)
        header.setOnClickListener {
            supportActionBar?.title="My Profile"
            inflateFragment(Profile())
            navigationView.setCheckedItem(R.id.my_profile)
        }
        val tv_user = header.findViewById<TextView>(R.id.username_drawer)
        val tv_mobile = header.findViewById<TextView>(R.id.phoneNumber_drawer)
        tv_user.text = username
        tv_mobile.text =mobile

        navigationView.setCheckedItem(R.id.home)

        navigationView.setNavigationItemSelectedListener {


            if (previousMenuItem!= null){
                previousMenuItem?.isChecked=false
            }


            it.isCheckable =true
            it.isChecked = true
            previousMenuItem=it

            when(it.itemId){

                R.id.home -> {
                    supportActionBar?.title="Restaurants"
                    inflateFragment(Restaurants())
                }
                R.id.my_profile -> {
                    supportActionBar?.title="My Profile"
                    inflateFragment(Profile())
                }
                R.id.fav_rest -> {
                    supportActionBar?.title="Favourite Restaurants"
                    inflateFragment(FavoritesRestaurants())
                }
                R.id.order_history -> {
                    supportActionBar?.title="Order History"
                    inflateFragment(OrderHistory())
                }
                R.id.faqs -> {
                    supportActionBar?.title="FAQs"
                    inflateFragment(FAQs())
                }
                R.id.log_out -> {
                    val dialog = AlertDialog.Builder(this@Dashboard)
                    dialog.setTitle("Logout")
                    dialog.setMessage("Do you want to exit ")

                    dialog.setNegativeButton("NO"){ dialogInterface: DialogInterface, i: Int ->
                        supportActionBar?.title="Restaurants"
                        inflateFragment(Restaurants())
                        navigationView.setCheckedItem(R.id.home)
                    }

                    dialog.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                        sharedPreferences.edit().putBoolean("isLogged",false).apply()
                        startActivity(Intent(this@Dashboard,LoginPage::class.java))
                        finish()
                    }
                    dialog.setCancelable(false)
                    setFinishOnTouchOutside(false)
                    dialog.create()
                    dialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }

    }


    private fun inflateFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragment).commit()
        drawerLayout.closeDrawers()
    }

    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frame_layout)

        if(frag !is Restaurants) {
            inflateFragment(Restaurants())
            navigationView.setCheckedItem(R.id.home)
        }else{
            finishAffinity()
//            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

         val id = item.itemId

         if (id==android.R.id.home){
             drawerLayout.openDrawer(GravityCompat.START)
         }
         return super.onOptionsItemSelected(item)

    }
}