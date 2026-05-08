package com.example.mygame.views

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OptionUi(
    val id: Long,
    val title: String
) : Parcelable