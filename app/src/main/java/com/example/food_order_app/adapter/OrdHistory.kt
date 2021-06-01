package com.example.food_order_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.database.ResCartItem

class OrdHistory(val context: Context,val array: ArrayList<Any>) : RecyclerView.Adapter<OrdHistory.OrdHisHolder>() {

    class OrdHisHolder(view: View) : RecyclerView.ViewHolder(view) {
        val order_res: TextView = view.findViewById(R.id.ordered_res)
        val order_date: TextView = view.findViewById(R.id.order_date)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_items)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdHisHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_row_order_history, parent, false)
        return OrdHisHolder(view)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: OrdHisHolder, position: Int) {
        val array = array[position] as ArrayList<*>
        holder.order_res.text = array[0].toString()
        holder.order_date.text = array[2].toString().subSequence(0,8)
        val adapter: RecyclerView.Adapter<CartAdapter.CartViewHolder>
        val arr: ArrayList<ResCartItem> = array[3] as ArrayList<ResCartItem>
        holder.recyclerView.layoutManager = LinearLayoutManager(context)
        holder.recyclerView.adapter = CartAdapter(context, arr)
    }


}