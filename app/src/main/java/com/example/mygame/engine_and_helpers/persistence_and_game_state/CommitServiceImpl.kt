package com.example.mygame.engine_and_helpers.persistence_and_game_state

import com.example.mygame.dao.foundations_core.TurnClockDao
import com.example.mygame.dao.persistence_and_game_state.WorldCommitDao
import com.example.mygame.database.persistence_and_game_state.WorldCommitEntity
import com.example.mygame.engine_and_helpers.ChecksumUtil
import com.example.mygame.engine_and_helpers.TimeProvider
import com.example.mygame.engine_and_helpers.TxRunner
import com.example.mygame.engine_and_helpers.foundations_core.AuditLogger
import javax.inject.Inject

interface CommitService {
    /** Writes a world commit for current turn with a checksum derived from [WorldSnapshotProvider]. */
    suspend fun commitTurn()
    suspend fun lastCommit(): WorldCommitEntity?
}

class CommitServiceImpl @Inject constructor(
    private val commitDao: WorldCommitDao,
    private val clockDao: TurnClockDao,
    private val snapshot: WorldSnapshotProvider,
    private val time: TimeProvider,
    private val tx: TxRunner,
    private val audit: AuditLogger
) : CommitService {
    override suspend fun commitTurn() = tx.tx {
        val turn = clockDao.getSingleton()?.turn ?: 0
        val checksum = ChecksumUtil.sha256(snapshot.snapshot())
        commitDao.commit(turn, checksum, time.nowMillis())
        audit.log("WORLD_COMMIT", mapOf("turn" to turn, "checksum" to checksum))
    }

    override suspend fun lastCommit() = commitDao.lastCommit()
}
