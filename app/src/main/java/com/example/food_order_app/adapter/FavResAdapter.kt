package com.example.food_order_app.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.activity.MenuItems
import com.example.food_order_app.database.ResEntity
import com.example.food_order_app.fragment.FavoritesRestaurants
import com.example.food_order_app.fragment.Restaurants
import com.squareup.picasso.Picasso


class FavResAdapter(val context: Context, val list :List<ResEntity>) : RecyclerView.Adapter<FavResAdapter.FavResHolder>() {

    lateinit var sharedPreferences : SharedPreferences

    class FavResHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_dish_name = view.findViewById<TextView>(R.id.txt_dish_name)
        val txt_dish_cost = view.findViewById<TextView>(R.id.txt_dish_cost)
        val rb_rating: RatingBar = view.findViewById(R.id.rb_dish_rating)
        val image: ImageView = view.findViewById(R.id.img_info)
        val layout: LinearLayout = view.findViewById(R.id.HomeLayout)
        val addFavButton: ImageView = view.findViewById(R.id.img_addtofav)
        val rmFromFav: ImageView = view.findViewById(R.id.img_rm_fav)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavResHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_single_row, parent, false)

        return FavResAdapter.FavResHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FavResAdapter.FavResHolder, position: Int) {
        val list = list[position]
        holder.txt_dish_name.text = list.name
        holder.txt_dish_cost.text = list.cost_for_one
        holder.rb_rating.rating = list.rating.toFloat()
        Picasso.get().load(list.image_url).into(holder.image)
        val resEntity = ResEntity(
                list.id,
                list.name,
                list.rating,
                list.cost_for_one,
                list.image_url
        )
        holder.layout.setOnClickListener {
            val intent = Intent(context, MenuItems::class.java)
            HomeAdapter.asyn(context).execute()
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

        holder.addFavButton.setOnClickListener {
                val del = Restaurants.asynTask(context, resEntity, 2).execute().get()
                if (del) {
                    holder.rmFromFav.visibility = View.VISIBLE
                    holder.addFavButton.visibility = View.GONE
                    Toast.makeText(context, "Restaurant removed from Favorites", Toast.LENGTH_LONG).show()
                }

            val ac = context as Activity
            ac.getFragmentManager().popBackStack();
            val activity = context as AppCompatActivity
            val myFragment: Fragment = FavoritesRestaurants()
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, myFragment).commit();
        }
    }
}