package com.example.mygame.engine_and_helpers.roles_and_offices

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mygame.engine_and_helpers.test_seeder.ApplicationScope
import com.example.mygame.engine_and_helpers.test_seeder.DbSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDbCallback @Inject constructor(
    private val seeder: DbSeeder,
    @ApplicationScope private val appScope: CoroutineScope
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        appScope.launch { seeder.seedAllIfEmpty() }
    }
}