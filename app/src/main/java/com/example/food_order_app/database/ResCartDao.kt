package com.example.food_order_app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResCartDao {
    @Insert
    fun insertCartItem(resCartItem: ResCartItem)

    @Query ("SELECT * FROM ResCartItem")
    fun getCartItem():List<ResCartItem>

    @Query ("DELETE FROM ResCartItem ")
    fun deleteCart()

    @Query("SELECT * FROM ResCartItem WHERE food_item_id=:Id")
    fun isCartItem(Id: String) : Boolean

    @Query("DELETE FROM ResCartItem WHERE food_item_id=:Id")
    fun cartItemDelete(Id: String)

    @Query("SELECT COUNT(*) FROM ResCartItem")
    fun getNoItems() : Int

    @Query("SELECT SUM(dish_cost) FROM ResCartItem")
    fun getTotal() : Int

    @Query ("SELECT food_item_id FROM ResCartItem")
    fun getids() : List <PlaceOrder>

}