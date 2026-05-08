package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "title_track",
    indices = [Index(value = ["titleId"], name = "idx_title_track_title")]
)
data class TitleTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titleId: Long,
    val nextTitleId: Long?
)