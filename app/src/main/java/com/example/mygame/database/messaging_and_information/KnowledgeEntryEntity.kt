package com.example.mygame.database.messaging_and_information

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "knowledge_entry",
    indices = [Index(value = ["actorId"], name = "idx_knowledge_actor")]
)
data class KnowledgeEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorId: Long,
    val fact: String,
    val sourceMsgId: Long,
    val turn: Int,
    val confirmed: Boolean
)
//for actors location naming convention of field "fact" is this
//ACTOR_AT:<actorId>:LAND=<landId>:IMPRISONED=<0|1>
//ACTOR_NAME:<actorId>:<name>

//for roles assignment naming convention of field "fact" is this
//ROLE_HOLDER:CHANCELLOR:ACTOR=12
//ROLE_HOLDER:DEFENSE_COMMANDER:ACTOR=18
//ROLE_HOLDER:COIN_HOLDER:ACTOR=21