package com.example.food_order_app.fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.text.toSpannable
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import com.example.food_order_app.adapter.HomeAdapter
import com.example.food_order_app.database.FavResDatabase
import com.example.food_order_app.database.ResEntity
import com.example.food_order_app.model.Info
import com.example.food_order_app.util.Connectivity
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Restaurants : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var obj: Info
    lateinit var res_list: ArrayList<Info>
    lateinit var adapter: RecyclerView.Adapter<HomeAdapter.HomeViewHolder>
    lateinit var addFavButton: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var searchView: SearchView
    lateinit var adapter1: ArrayAdapter<Info>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)
        val Connectivity = Connectivity()
        HomeAdapter.asyn(activity as Context).execute()
        if (Connectivity.checkConnectivity(activity as Context)) {

            recyclerView = view.findViewById(R.id.recycler_home)
            progressLayout = view.findViewById(R.id.progressLayout)
            progressBar = view.findViewById(R.id.progressbar)
            searchView = view.findViewById(R.id.searchView)
            layoutManager = GridLayoutManager(activity as Context, 1)
            setHasOptionsMenu(true)
            res_list = ArrayList()


            val queue = Volley.newRequestQueue(activity as Context)

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val requestObject =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {

                        progressLayout.visibility = View.GONE

                        val datajsonObject = it.getJSONObject("data")
                        val success = datajsonObject.getBoolean("success")
                        if (success) {
                            val jsonArray = datajsonObject.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val obj = Info(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("rating"),
                                    jsonObject.getString("cost_for_one"),
                                    jsonObject.getString("image_url")
                                )

                                res_list.add(obj)
                                adapter = HomeAdapter(activity as Context, res_list)
                                recyclerView.layoutManager = layoutManager
                                recyclerView.adapter = adapter
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Unknown Error occured",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (json: JSONException) {
                        Toast.makeText(activity as Context, "Json error $json", Toast.LENGTH_LONG)
                            .show()
                    }

                    adapter1 = ArrayAdapter<Info>(activity as Context,android.R.layout.simple_list_item_1,res_list)


                }, Response.ErrorListener {
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

            queue.add(requestObject)
        } else {
            DialogInternet(activity as Context)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText)
                return false
            }
        })




        return view

    }

    private fun filter(newText: String?) {
        val arr : ArrayList<Info>
        arr= arrayListOf()

        for( i in res_list){
            if(i.name.toString().toLowerCase().contains(newText?.toLowerCase()!!)){
                arr.add(i)
            }
            else{
                adapter = HomeAdapter(activity as Context, arr)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

        }
    }


    class asynTask(val context: Context, val resEntity: ResEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, FavResDatabase::class.java, "FavRes").build()
            when (mode) {
                1 -> {
                    db.resDao().insertResFav(resEntity)
                    db.close()
                    return true
                }
                2 -> {
                    db.resDao().removeResFav(resEntity)
                    db.close()
                    return true
                }
                3 -> {
                    val result = db.resDao().getResId(resEntity.id)
                    db.close()
                    return result != null
                }
                4 -> {
                    db.resDao().getListOfRes()
                    db.close()
                    return true
                }
            }

            db.close()
            return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_items, menu)
        for (i in menu) {
            val s = i.title.toSpannable()
            s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
            i.title = s
        }
    }

    var costComparator = Comparator<Info> { info1, info2 ->
        if (info1.cost_for_one.compareTo(info2.cost_for_one, true) == 0)
            info1.name.compareTo(info2.name, true)
        else
            info1.cost_for_one.compareTo(info2.cost_for_one, true)
    }

    var ratingComparator = Comparator<Info> { info1, info2 ->
        if (info1.rating.compareTo(info2.rating, true) == 0)
            info1.name.compareTo(info2.name, true)
        else
            info1.rating.compareTo(info2.rating, true)
    }

    fun sort() {
        val arr = arrayOf<String>("Cost (High to Low)", "Cost (Low to High)", "Rating")
        var selected = 10
        val dialog = AlertDialog.Builder(activity as Context)
        dialog.setTitle("Sort By ??")
        val context = activity as Context
        dialog.setSingleChoiceItems(arr, -1,
            DialogInterface.OnClickListener { Dialog, Item ->
                selected = Item
            })
        dialog.setNegativeButton("Cancel", null)
        dialog.setPositiveButton(
            "Okay", DialogInterface.OnClickListener(
            ) { dialogInterface: DialogInterface, i: Int ->
                when (selected) {
                    0 -> {
                        Collections.sort(res_list, costComparator)
                        res_list.reverse()
                    }
                    1 -> Collections.sort(res_list, costComparator)
                    2 -> {
                        Collections.sort(res_list, ratingComparator)
                        res_list.reverse()
                    }
                    else -> {
                        Toast.makeText(
                            activity as Context,
                            "No option selected for sorting",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }
        )
        dialog.setCancelable(false)
        activity?.setFinishOnTouchOutside(false)
        dialog.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        when (id) {
            R.id.sort -> sort()
        }
        return super.onOptionsItemSelected(item)
    }

    fun DialogInternet(context: Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setCancelable(false)
        dialog.setTitle("CHECKING INTERNET !!!!")
        dialog.setMessage("No internet Connection,Please coonect to INTERNET and reopen the app")
        dialog.setNegativeButton("Exit") { text, listener ->
            finishAffinity(context as Activity)
        }
        dialog.setPositiveButton("SETTINGS") { text, listener ->
            context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            finishAffinity(context as Activity)
        }
        activity?.setFinishOnTouchOutside(false)
        dialog.create()
        dialog.show()
    }
}
