package com.example.food_order_app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao {
    @Insert
    fun insertResFav(resEntity: ResEntity)

    @Delete
    fun removeResFav(resEntity: ResEntity)

    @Query("SELECT * FROM FavRes WHERE id=:ResId")
    fun getResId(ResId: String): ResEntity

    @Query("SELECT * FROM FavRes")
    fun getListOfRes(): List<ResEntity>
}
