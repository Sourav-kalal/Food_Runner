package com.example.food_order_app.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.food_order_app.R
import com.example.food_order_app.activity.Cart
import com.example.food_order_app.database.ResCartDatabase
import com.example.food_order_app.database.ResCartItem
import com.example.food_order_app.model.ResMenuItems

class MenuListAdapter(
    val context: Context,
    val list: ArrayList<ResMenuItems>,
    val relativeLayout: RelativeLayout,
    val frameLayout: FrameLayout,
    val resId : String?
) : RecyclerView.Adapter<MenuListAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menu_item_id: TextView = view.findViewById(R.id.txt_menu_item_id)
        val menu_item_name: TextView = view.findViewById(R.id.txt_menu_item_name)
        val menu_item_price: TextView = view.findViewById(R.id.txt_menu_item_price)
        val btn_add_menu: Button = view.findViewById(R.id.btn_menu_item)
        val cardView : CardView = view.findViewById(R.id.cv_single_menu_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item_single_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val Menu_items = list[position]

        val btn = relativeLayout.findViewById<Button>(R.id.btn_prcd_cart)
        holder.menu_item_id.text = Menu_items.id.toString()
        holder.menu_item_name.text = Menu_items.name
        holder.menu_item_price.text = Menu_items.cost_for_one

        var resCartItem1 = ResCartItem(
            Menu_items.id,
            Menu_items.name,
            Menu_items.cost_for_one.toInt()
        )

        var isCartItem = asynTask(context, resCartItem1, 2).execute().get()
        if (!(isCartItem)) {
            holder.btn_add_menu.text = context.getString(R.string.add)
            holder.btn_add_menu.setBackgroundColor(ContextCompat.getColor(context,R.color.primaryColor))
        }else{
            holder.btn_add_menu.text = context.getString(R.string.remove)
            holder.btn_add_menu.setBackgroundColor(Color.BLACK)
        }


        holder.btn_add_menu.setOnClickListener {

            btn.setOnClickListener {
                val intent = Intent(context, Cart::class.java)
                intent.putExtra("Res_id",resId)
                context.startActivity(intent)
            }

            resCartItem1 = ResCartItem(
                Menu_items.id,
                Menu_items.name,
                Menu_items.cost_for_one.toInt()
            )

            isCartItem = asynTask(context, resCartItem1, 2).execute().get()
            if (isCartItem) {
                holder.btn_add_menu.text = context.getString(R.string.add)
                holder.btn_add_menu.setBackgroundColor(ContextCompat.getColor(context,R.color.primaryColor))
                asynTask(context, resCartItem1, 3).execute().get()
            } else {
                println(resCartItem1)
                holder.btn_add_menu.text = context.getString(R.string.remove)
                holder.btn_add_menu.setBackgroundColor(Color.BLACK)
                asynTask(context, resCartItem1, 1).execute().get()
            }

            val cartItem = asynTaskgetItem(context).execute().get()

            if (cartItem > 0) {
                frameLayout.setPadding(0,0,0,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45F, context.resources.displayMetrics).toInt())
                btn.visibility = View.VISIBLE
            }else{
                frameLayout.setPadding(0,0,0,0)
                btn.visibility = View.GONE
            }
        }
    }

    class asynTask(val context: Context, val resCartItem: ResCartItem, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db =
                Room.databaseBuilder(context, ResCartDatabase::class.java, "ResMenuItem").build()
            when (mode) {
                1 -> {
                    db.resCartDao().insertCartItem(resCartItem)
                    db.close()
                    return true
                }
                2 -> {
                    val result= db.resCartDao().isCartItem(resCartItem.food_item_id)
                    db.close()
                    return result
                }
                3 -> {
                    db.resCartDao().cartItemDelete(resCartItem.food_item_id)
                    db.close()
                    return true
                }
            }
            return false
        }
    }


        class asynTaskgetItem(val context: Context) :
            AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void?): Int {
                val db =
                    Room.databaseBuilder(context, ResCartDatabase::class.java, "ResMenuItem")
                        .build()
                val res = db.resCartDao().getNoItems()
                db.close()
                return res
            }

        }
    }