package com.example.food_order_app.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "order_item")
data class PlaceOrder(
    @PrimaryKey val food_item_id : String
)