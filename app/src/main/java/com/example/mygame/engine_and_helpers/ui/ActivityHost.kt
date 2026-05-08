package com.example.mygame.engine_and_helpers.ui


import android.app.Activity
import java.lang.ref.WeakReference

object ActivityHost {
    @Volatile private var ref: WeakReference<Activity>? = null
    fun set(activity: Activity?) { ref = activity?.let { WeakReference(it) } }
    fun get(): Activity? = ref?.get()
}