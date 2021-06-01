package com.example.food_order_app.fragment

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.text.toSpannable
import androidx.core.view.iterator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.food_order_app.R
import com.example.food_order_app.adapter.FavResAdapter
import com.example.food_order_app.database.FavResDatabase
import com.example.food_order_app.database.ResEntity
import com.example.food_order_app.model.Info
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class FavoritesRestaurants : Fragment() {

    lateinit var recyclerView: RecyclerView

    lateinit var layout: RecyclerView.LayoutManager
    lateinit var adapter: RecyclerView.Adapter<FavResAdapter.FavResHolder>
    lateinit var res_list : List<ResEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites_restaurants, container, false)

        recyclerView = view.findViewById(R.id.recycler_favres)

        setHasOptionsMenu(true)
        res_list = retriveData(activity as Context).execute().get()
        adapter = FavResAdapter(context = activity as Context, list = res_list)
        layout = GridLayoutManager(activity as Context, 1)
        recyclerView.layoutManager = layout
        recyclerView.adapter = adapter
        return view
    }
        class retriveData(val context: Context) : AsyncTask<Void, Void, List<ResEntity>>() {
            override fun doInBackground(vararg params: Void?): List<ResEntity> {
                val db = Room.databaseBuilder(context, FavResDatabase::class.java, "FavRes").build()
                return db.resDao().getListOfRes()
            }

        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_items,menu)
        for (i in menu){
            val s = i.title.toSpannable()
            s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
            i.title = s
        }
    }
    var costComparator = Comparator<ResEntity> { info1, info2 ->
        if(info1.cost_for_one.compareTo(info2.cost_for_one,true)==0)
            info1.name.compareTo(info2.name,true)
        else
            info1.cost_for_one.compareTo(info2.cost_for_one,true)
    }

    var ratingComparator = Comparator<ResEntity> { info1, info2 ->
        if(info1.rating.compareTo(info2.rating,true)==0)
            info1.name.compareTo(info2.name,true)
        else
            info1.rating.compareTo(info2.rating,true)
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
                val arr1 = res_list as ArrayList<Info>

                when (selected) {
                    0 -> {
                        Collections.sort(res_list, costComparator)
                        arr1.reverse()
                    }
                    1 -> Collections.sort(res_list, costComparator)
                    2 -> {
                        Collections.sort(res_list, ratingComparator)
                        arr1.reverse()
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


}