package com.example.mygame.engine_and_helpers

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.example.mygame.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TxRunner @Inject constructor(
    private val db: AppDatabase
) {
    suspend fun <T> tx(block: suspend () -> T): T = db.withTransaction { block() }
}