package com.example.food_order_app.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResEntity::class,ResCartItem::class],version = 1)
abstract class FavResDatabase : RoomDatabase() {
    abstract fun  resDao() : ResDao
}