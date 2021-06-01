package com.example.food_order_app.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.food_order_app.R
import com.example.food_order_app.activity.MenuItems
import com.example.food_order_app.database.ResCartDatabase
import com.example.food_order_app.database.ResEntity
import com.example.food_order_app.fragment.Restaurants
import com.example.food_order_app.model.Info
import com.squareup.picasso.Picasso

open class HomeAdapter(val context: Context, var list: ArrayList<Info>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    lateinit var sharedPreferences: SharedPreferences

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_dish_name = view.findViewById<TextView>(R.id.txt_dish_name)
        val txt_dish_cost = view.findViewById<TextView>(R.id.txt_dish_cost)
        val rb_rating: RatingBar = view.findViewById(R.id.rb_dish_rating)
        val image: ImageView = view.findViewById(R.id.img_info)
        val layout: LinearLayout = view.findViewById(R.id.HomeLayout)
        val addFavButton: ImageView = view.findViewById(R.id.img_addtofav)
        val rmFromFav: ImageView = view.findViewById(R.id.img_rm_fav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val list = list[position]
        holder.txt_dish_name.text = list.name
        holder.txt_dish_cost.text = String.format(context.getString(R.string.item_amout_menu),list.cost_for_one)
        holder.rb_rating.rating = list.rating.toFloat()
        Picasso.get().load(list.image_url).error(R.drawable.img_logo).into(holder.image)

        holder.layout.setOnClickListener {
            val intent = Intent(context, MenuItems::class.java)
            asyn(context).execute()
            sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sharedPref),
                Context.MODE_PRIVATE
            )!!
            sharedPreferences.edit().putString("restaurant_id", list.id).apply()
            sharedPreferences.edit().putString("restaurant_name", list.name).apply()
            intent.putExtra("id", list.id)
            intent.putExtra("name", list.name)
            context.startActivity(intent)
        }

        val resEntity = ResEntity(
            list.id,
            list.name,
            list.rating,
            list.cost_for_one,
            list.image_url
        )
        val asyn = Restaurants.asynTask(context, resEntity, 3).execute()
        val res = asyn.get()
        if (res) {
            holder.rmFromFav.visibility = View.GONE
            holder.addFavButton.visibility = View.VISIBLE
        } else {
            holder.rmFromFav.visibility = View.VISIBLE
            holder.addFavButton.visibility = View.GONE
        }
        holder.addFavButton.setOnClickListener {
            val del = Restaurants.asynTask(context, resEntity, 2).execute().get()
            if (del) {
                holder.rmFromFav.visibility = View.VISIBLE
                holder.addFavButton.visibility = View.GONE
                Toast.makeText(context, "Restaurant removed from Favorites", Toast.LENGTH_LONG)
                    .show()
            }
        }
        holder.rmFromFav.setOnClickListener {
            val insert = Restaurants.asynTask(context, resEntity, 1).execute().get()
            if (insert) {
                holder.rmFromFav.visibility = View.GONE
                holder.addFavButton.visibility = View.VISIBLE
                Toast.makeText(context, "Restaurant Added To Favorites", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    class asyn(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db =
                Room.databaseBuilder(context, ResCartDatabase::class.java, "ResMenuItem").build()
            db.resCartDao().deleteCart()
            db.close()
            return true
        }
    }
}