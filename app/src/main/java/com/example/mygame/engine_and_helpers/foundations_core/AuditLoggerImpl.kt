package com.example.mygame.engine_and_helpers.foundations_core

import com.example.mygame.dao.foundations_core.AuditLogDao
import com.example.mygame.dao.foundations_core.TurnClockDao
import com.example.mygame.database.foundations_core.AuditLogEntity
import com.example.mygame.engine_and_helpers.ChecksumUtil
import com.example.mygame.engine_and_helpers.TimeProvider
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

interface AuditLogger {
    suspend fun log(type: String, payload: Map<String, Any?> = emptyMap())
}
//class AuditLoggerImpl(
//    private val dao: AuditLogDao,
//    private val clockDao: TurnClockDao,
//    private val time: TimeProvider
//) : AuditLogger {
//    override suspend fun log(type: String, payload: Map<String, Any?>) {
//        val turn = clockDao.getSingleton()?.turn ?: 0
//        val json = payload.entries.joinToString(prefix = "{", postfix = "}") { (k, v) ->
//            "\"$k\":\"${v?.toString() ?: "null"}\""
//        }
//        val hash = ChecksumUtil.sha256("$type@$turn#$json@${time.nowMillis()}".encodeToByteArray())
//        dao.upsert(AuditLogEntity(0, turn, null, type, json, hash, time.nowMillis()))
//    }
//}


@Singleton
class AuditLoggerImpl @Inject constructor(
    private val auditDao: AuditLogDao,
    private val clockDao: TurnClockDao,
    private val time: TimeProvider
) : AuditLogger {

    override suspend fun log(type: String, payload: Map<String, Any?>) {
        val turn = clockDao.getSingleton()?.turn ?: 0
        val json = payload.entries.joinToString(prefix = "{", postfix = "}") { (k, v) ->
            // very simple JSON encoding to match your previous style
            val value = v?.toString() ?: "null"
            "\"$k\":\"$value\""
        }
        val stamp = time.nowMillis()
        val hash = sha256("$type@$turn#$json@$stamp")
        auditDao.upsert(
            AuditLogEntity(
                id = 0,
                turn = turn,
                actorId = null,
                action = type,
                payloadJson = json,
                hash = hash,
                createdAt = stamp
            )
        )
    }

    private fun sha256(s: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(s.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}