package com.example.food_order_app.activity

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food_order_app.R
import com.example.food_order_app.adapter.MenuListAdapter
import com.example.food_order_app.database.FavResDatabase
import com.example.food_order_app.model.ResMenuItems
import org.json.JSONException

class MenuItems : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: RecyclerView.Adapter<MenuListAdapter.MenuViewHolder>
    lateinit var list: ArrayList<ResMenuItems>
    lateinit var toolbar: Toolbar
    lateinit var button: Button
    lateinit var frameLayout: FrameLayout
    lateinit var lyut_rel: RelativeLayout
    lateinit var relativeLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var tv_msg_mi : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_items)

        list = arrayListOf()

        frameLayout = findViewById(R.id.frame_layout_menu)

        lyut_rel = findViewById(R.id.rel_menu_item)

        tv_msg_mi = findViewById(R.id.tv_msg_mi)

        recyclerView = findViewById(R.id.MenuRecycler)
        layoutManager = LinearLayoutManager(this@MenuItems)
        toolbar = findViewById(R.id.tool_bar_menu)
        button = findViewById(R.id.btn_prcd_cart)
        button.visibility = View.GONE

        relativeLayout = findViewById(R.id.progressLayout_menu)
        progressBar = findViewById(R.id.progressbar_menu)

        relativeLayout.visibility = View.VISIBLE
        relativeLayout.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(this@MenuItems)
        val id = intent.getStringExtra("id")

        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("name")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url + id, null, Response.Listener {
                try {
                    val asynTask = isFav(this@MenuItems,id!!)
                    val isfav = asynTask.execute().get()
                    if(isfav){
                        val fav = AppCompatResources.getDrawable(this@MenuItems,R.drawable.ic_fav_sel)!!
                        val wrapdraw = DrawableCompat.wrap(fav)
                        DrawableCompat.setTint(wrapdraw,Color.DKGRAY)
                        tv_msg_mi.setCompoundDrawablesWithIntrinsicBounds(null,null,wrapdraw,null)
                    }else{
                        val fav = AppCompatResources.getDrawable(this@MenuItems,R.drawable.ic_fav_not_sel)!!
                        val wrapdraw = DrawableCompat.wrap(fav)
                        DrawableCompat.setTint(wrapdraw,Color.DKGRAY)
                        tv_msg_mi.setCompoundDrawablesWithIntrinsicBounds(null,null,wrapdraw,null)
                    }
                    val json = it.getJSONObject("data")
                    val reqstatus = json.getBoolean("success")
                    if (reqstatus) {
                        relativeLayout.visibility = View.GONE
                        relativeLayout.visibility = View.GONE
                        val jsonObject = json.getJSONArray("data")
                        for (i in 0 until jsonObject.length()) {
                            val obj = jsonObject.getJSONObject(i)
                            val menuitems = ResMenuItems(
                                obj.getString("id"),
                                obj.getString("name"),
                                obj.getString("cost_for_one")
                            )
                            list.add(menuitems)
                            adapter =
                                MenuListAdapter(this@MenuItems, list, lyut_rel, frameLayout, id)
                            recyclerView.layoutManager = layoutManager
                            recyclerView.adapter = adapter
                        }
                    } else {
                        Toast.makeText(
                            this@MenuItems,
                            "Some unknown Error ocuured",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (json: JSONException) {

                    Toast.makeText(this@MenuItems, "Json error $json", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this@MenuItems, "Volley Error $it", Toast.LENGTH_LONG).show()
            }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>()
                    header["Content-Type"] = "application/json"
                    header["token"] = "f2b826db087fde"
                    return header
                }
            }
        queue.add(jsonObjectRequest)
    }

    override fun onBackPressed() {

        val dialog = AlertDialog.Builder(this@MenuItems)
        dialog.setTitle("Do you want to go back ?")
        dialog.setMessage("If you go back cart will be deleted")
        dialog.setNegativeButton("No", null)
        dialog.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            super.onBackPressed()
        }
        dialog.create()
        dialog.show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)

    }

    class isFav(val context: Context, val resId: String) :
        AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, FavResDatabase::class.java, "FavRes").build()
            val result = db.resDao().getResId(resId)
            db.close()
            return result != null
        }
    }

}
