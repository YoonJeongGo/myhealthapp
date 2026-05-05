package com.healthlog.myapplication1.data.repository

import com.healthlog.myapplication1.data.local.dao.UserProfileDao
import com.healthlog.myapplication1.data.local.entity.UserProfileEntity

class UserRepository(private val dao: UserProfileDao) {

    suspend fun getProfile(): UserProfileEntity? = dao.get()

    suspend fun saveProfile(entity: UserProfileEntity) {
        val existing = dao.get()
        if (existing == null) dao.insert(entity)
        else dao.update(entity.copy(id = existing.id, createdAt = existing.createdAt))
    }

    suspend fun hasProfile(): Boolean = dao.get() != null
}
