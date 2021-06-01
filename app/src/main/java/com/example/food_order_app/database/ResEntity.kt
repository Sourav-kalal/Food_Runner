package com.example.food_order_app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "FavRes")
data class ResEntity(
    @PrimaryKey val id : String,
    @ColumnInfo (name="res_name") val name : String,
    @ColumnInfo (name="res_rating") val rating : String,
    @ColumnInfo (name="res_cost") val cost_for_one : String,
    @ColumnInfo (name="res_image") val image_url : String
)