package com.example.mygame.engine_and_helpers.world_and_geography

//import com.example.mygame.dao.foundations_core.TurnClockDao
//import com.example.mygame.dao.world_and_geography.LandDao
//import com.example.mygame.dao.world_and_geography.OwnershipDao
//import com.example.mygame.engine_and_helpers.persistence_and_game_state.WorldSnapshotProvider

// =============================
// Simple snapshot provider (example)
// =============================

//class MinimalWorldSnapshotProvider(
//    private val landDao: LandDao,
//    private val ownershipDao: OwnershipDao,
//    private val clockDao: TurnClockDao
//) : WorldSnapshotProvider {
//    override suspend fun snapshot(): ByteArray {
//        val turn = clockDao.getSingleton()?.turn ?: 0
//        val lands = landDao.byCountry(0) // caller can inject a richer impl; here just serialize existence deterministically
//        val owners = lands.mapNotNull { ownershipDao.ownerOf(it.id) }
//        val raw = buildString {
//            append("turn=").append(turn).append('\n')
//            lands.sortedBy { it.id }.forEach { append("L:").append(it.id).append(':').append(it.name).append('\n') }
//            owners.sortedBy { it.landId }.forEach { append("O:").append(it.landId).append(':').append(it.ownerType).append(':').append(it.ownerRef).append('\n') }
//        }
//        return raw.encodeToByteArray()
//    }
//}

import com.example.mygame.dao.foundations_core.TurnClockDao
import com.example.mygame.dao.world_and_geography.LandDao
import com.example.mygame.dao.world_and_geography.OwnershipDao
import com.example.mygame.engine_and_helpers.persistence_and_game_state.WorldSnapshotProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@Singleton
class MinimalWorldSnapshotProvider @Inject constructor(
    private val landDao: LandDao,
    private val ownershipDao: OwnershipDao,
    private val clockDao: TurnClockDao
) : WorldSnapshotProvider {

    override fun snapshot(): ByteArray = runBlocking(Dispatchers.IO) {
        val turn = clockDao.getSingleton()?.turn ?: 0
        val lands = landDao.byCountry(0)                    // suspend DAO
        val owners = lands.mapNotNull { ownershipDao.ownerOf(it.id) } // suspend DAO
        val raw = buildString {
            append("turn=").append(turn).append('\n')
            lands.sortedBy { it.id }
                .forEach { append("L:").append(it.id).append(':').append(it.name).append('\n') }
            owners.sortedBy { it.landId }
                .forEach { append("O:").append(it.landId).append(':')
                    .append(it.ownerType).append(':').append(it.ownerRef).append('\n') }
        }
        raw.encodeToByteArray()
    }
}
