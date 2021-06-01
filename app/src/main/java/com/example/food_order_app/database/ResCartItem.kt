package com.example.food_order_app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ResCartItem")
data class ResCartItem(
    @PrimaryKey val food_item_id: String,
    @ColumnInfo (name = "dish_name") val name: String,
    @ColumnInfo (name = "dish_cost") val cost: Int
)