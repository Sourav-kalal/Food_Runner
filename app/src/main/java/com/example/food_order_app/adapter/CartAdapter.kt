package com.example.food_order_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.database.ResCartItem

class CartAdapter(val context: Context, val arrayList: List<ResCartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dish_name: TextView = view.findViewById(R.id.txt_dish_name_cart)
        val dish_cost: TextView = view.findViewById(R.id.txt_dish_cost_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val list = arrayList[position]
        holder.dish_name.text = list.name
        holder.dish_cost.text = String.format(context.getString(R.string.item_amout),list.cost.toString())
    }
}
