package com.example.food_order_app.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import com.example.food_order_app.adapter.CartAdapter
import com.example.food_order_app.database.ResCartDatabase
import com.example.food_order_app.database.ResCartItem
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Cart : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: RecyclerView.Adapter<CartAdapter.CartViewHolder>
    lateinit var button: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var ord_frm: TextView
    lateinit var toolbar_cart: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar_cart = findViewById(R.id.tool_bar_cart)

        recyclerView = findViewById<RecyclerView>(R.id.cart_recycler)
        sharedPreferences =
            getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)

        val resCartItem = asynTask(this@Cart).execute().get()
        button = findViewById(R.id.btn_Total)

        setSupportActionBar(toolbar_cart)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Cart"


        ord_frm = findViewById(R.id.restaurant_name_cart)
        val restaurant_name = sharedPreferences.getString("restaurant_name", "error")

        val strg = getString(R.string.ordering_from)
        val index = strg.indexOf("%1\$s")

        val str = String.format(getString(R.string.ordering_from), restaurant_name)
        val len = str.length
        val sp = SpannableString(str)
        sp.setSpan(StyleSpan(Typeface.BOLD), index, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ord_frm.text = sp

        println(sp)


        val total = asynTotal(this@Cart, 1).execute().get().toString()

        button.text = String.format(getString(R.string.place_order_total), total)


        button.setOnClickListener {

            val dialog = AlertDialog.Builder(this@Cart)
            dialog.setTitle("PLACE ORDER")
            dialog.setMessage("Do you want to place order")
            dialog.setNegativeButton("No", null)
            dialog.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->

                val myobj = asynTotal(this@Cart, 2).execute().get() as List<*>
                val gson = Gson()
                val food = gson.toJson(myobj)

                val user_id = sharedPreferences.getString("user_id", "")
                val restaurant_id = sharedPreferences.getString("restaurant_id", "")

                val json = JSONObject()
                println(food)
                json.put("user_id", user_id)
                json.put("restaurant_id", restaurant_id)
                json.put("total_cost", total)
                json.put("food", JSONArray(food))

                println(json)

                val queue = Volley.newRequestQueue(this@Cart)

                val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, json, Response.Listener {
                        try {
                            val intent = Intent(
                                this@Cart,
                                OrderPlaced::class.java
                            )
                            val jsonObject = it.getJSONObject("data")
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                intent.putExtra("total", total)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@Cart,
                                    "Unknown Error ocured",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (json: JSONException) {
                            Toast.makeText(this@Cart, "JSON Error $json", Toast.LENGTH_LONG).show()
                        }
                    },
                        Response.ErrorListener {
                            Toast.makeText(this@Cart, "Volley Error $it", Toast.LENGTH_LONG)
                                .show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val h = HashMap<String, String>()
                            h["Content-Type"] = "application/json"
                            h["token"] = "f2b826db087fde"
                            return h
                        }
                    }
                queue.add(jsonObjectRequest)

            }
            dialog.create()
            dialog.show()
        }
        layoutManager = LinearLayoutManager(this@Cart)
        adapter = CartAdapter(this@Cart, resCartItem)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    class asynTask(val context: Context) : AsyncTask<Void, Void, List<ResCartItem>>() {
        override fun doInBackground(vararg params: Void?): List<ResCartItem> {
            val db =
                Room.databaseBuilder(context, ResCartDatabase::class.java, "ResMenuItem").build()
            return db.resCartDao().getCartItem()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    class asynTotal(val context: Context, val mode: Int) : AsyncTask<Void, Void, Any>() {
        override fun doInBackground(vararg params: Void?): Any {
            val db =
                Room.databaseBuilder(context, ResCartDatabase::class.java, "ResMenuItem").build()

            when (mode) {
                1 -> {
                    val res = db.resCartDao().getTotal()
                    db.close()
                    return res
                }
                2 -> {
                    val ress = db.resCartDao().getids()
                    db.close()
                    return ress
                }
            }
            return true
        }
    }
}