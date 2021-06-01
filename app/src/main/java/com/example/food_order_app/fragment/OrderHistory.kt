package com.example.food_order_app.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import com.example.food_order_app.adapter.OrdHistory
import com.example.food_order_app.database.ResCartItem
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class OrderHistory : Fragment() {

    lateinit var sharedPreferences: SharedPreferences

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerView.Adapter<OrdHistory.OrdHisHolder>
    lateinit var layout: RecyclerView.LayoutManager
    lateinit var relativeLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        relativeLayout = view.findViewById(R.id.progressLayout_ord_his)
        progressBar = view.findViewById(R.id.progressbar_ord_his)

        relativeLayout.visibility = View.VISIBLE
        relativeLayout.visibility = View.VISIBLE

        sharedPreferences =
            activity?.getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)!!

        val user_id = sharedPreferences.getString("user_id", "")

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/" + user_id

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    val jsonObjectMain = it.getJSONObject("data")
                    val success = jsonObjectMain.getBoolean("success")
                    if (success) {

                        relativeLayout.visibility = View.GONE
                        relativeLayout.visibility = View.GONE
                        val jsonArray = jsonObjectMain.getJSONArray("data")
                        println(jsonArray)

                        var ArrayList = arrayListOf<Any>()
                        val ArrayofObjectList = arrayListOf<Any>()
                        var customObject = arrayListOf<Any>()
                        for (i in 0 until jsonArray.length()) {
                            println("jsonArray ${jsonArray[i]}")
                            val jsonObject: JSONObject = jsonArray[i] as org.json.JSONObject
                            ArrayList.add(jsonObject.get("restaurant_name"))
                            ArrayList.add(jsonObject.get("total_cost"))
                            ArrayList.add(jsonObject.get("order_placed_at"))
                            customObject.addAll(
                                Gson().fromJson(
                                    jsonObject.get("food_items").toString(),
                                    Array<ResCartItem>::class.java
                                )
                            )
                            ArrayList.add(customObject)
                            ArrayofObjectList.add(ArrayList)
                            customObject = arrayListOf()
                            ArrayList = arrayListOf<Any>()

                        }

                        val Divider =
                            DividerItemDecoration(activity as Context, LinearLayout.VERTICAL)
                        Divider.setDrawable(resources.getDrawable(R.drawable.listdivider))
                        recyclerView = view.findViewById(R.id.recycler_ord_his)
                        recyclerView.addItemDecoration(
                            Divider
                        )
                        layout = LinearLayoutManager(activity as Context)
                        adapter = OrdHistory(activity as Context, ArrayofObjectList)
                        recyclerView.layoutManager = layout
                        recyclerView.adapter = adapter

                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Some unknown Error occured",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                } catch (json: JSONException) {
                    Toast.makeText(activity as Context, "Json error $json", Toast.LENGTH_LONG)
                        .show()
                }
            },
                Response.ErrorListener {
                    Toast.makeText(activity as Context, "Volley Error $it", Toast.LENGTH_LONG)
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
                return view
            }

    }