package com.example.food_order_app.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ResCartItem::class],version = 2)
abstract class ResCartDatabase : RoomDatabase() {
    abstract fun resCartDao() : ResCartDao
}