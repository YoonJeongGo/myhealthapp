package com.healthlog.myapplication1.data.local.dao

import androidx.room.*
import com.healthlog.myapplication1.data.local.entity.UserProfileEntity

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun get(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserProfileEntity)

    @Update
    suspend fun update(entity: UserProfileEntity)
}