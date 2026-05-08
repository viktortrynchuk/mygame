package com.example.mygame.engine_and_helpers.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mygame.views.EighthView
import com.example.mygame.views.EleventhView
import com.example.mygame.views.FifteenthView
import com.example.mygame.views.FifthView
import com.example.mygame.views.FourteenthView
import com.example.mygame.views.FourthView
import com.example.mygame.views.NinthView
import com.example.mygame.views.SecondView
import com.example.mygame.views.SeventhView
import com.example.mygame.views.SixteenthView
import com.example.mygame.views.SixthView
import com.example.mygame.views.TenthView
import com.example.mygame.views.ThirdView
import com.example.mygame.views.ThirteenthView
import com.example.mygame.views.TwelfthView
import com.example.mygame.views.FirstView
import kotlin.reflect.KClass

class ActivityNavigator(
    private val appContext: Context // application context (safe fallback)
) : UiNavigator {

    override fun goTo(view: ViewId, args: Map<String, Any?>) {
        val activity = ActivityHost.get()
        val target = ActivityMap.activityFor(view) ?: run {
            val ctx = activity ?: appContext
            Toast.makeText(ctx, "View $view not implemented yet", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(activity ?: appContext, target.java).apply {
            if (activity == null) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtras(args.toBundle())
        }
        (activity ?: appContext).startActivity(intent)
    }
}

/** Map ViewId → Activity class. Add mappings as you implement screens. */
object ActivityMap {
    fun activityFor(view: ViewId): KClass<out AppCompatActivity>? = when (view) {
        ViewId.V1_ROOM_OR_TENT -> FirstView::class
        ViewId.V2_WORLD_MAP    -> SecondView::class
        ViewId.V12_MAIN_MENU   -> TwelfthView::class

        // Add the rest as you create activities:
         ViewId.V3_BATTLE_ROOM_OR_TENT -> ThirdView::class
         ViewId.V4_BATTLEFIELD_MAP     -> FourthView::class
         ViewId.V5_DUEL                -> FifthView::class
         ViewId.V6_BALL                -> SixthView::class
         ViewId.V7_HUNT                -> SeventhView::class
         ViewId.V8_FESTIVAL            -> EighthView::class
         ViewId.V9_AUDIENCE            -> NinthView::class
         ViewId.V10_VICTORY            -> TenthView::class
         ViewId.V11_DEFEAT             -> EleventhView::class
         ViewId.V13_TAVERN             -> ThirteenthView::class
         ViewId.V14_JUDGE              -> FourteenthView::class
         ViewId.V15_ARRESTED           -> FifteenthView::class
         ViewId.V16_MEAL               -> SixteenthView::class
        else -> null
    }
}

/** Convert a Map<String, Any?> to a Bundle for Intent extras. */
private fun Map<String, Any?>.toBundle(): Bundle {
    val b = Bundle()
    forEach { (k, v) ->
        when (v) {
            null -> Unit
            is Int -> b.putInt(k, v)
            is Long -> b.putLong(k, v)
            is Boolean -> b.putBoolean(k, v)
            is String -> b.putString(k, v)
            is Float -> b.putFloat(k, v)
            is Double -> b.putDouble(k, v)
            is Parcelable -> b.putParcelable(k, v)
            is java.io.Serializable -> b.putSerializable(k, v)
            is Array<*> -> when {
                v.isArrayOf<String>() -> b.putStringArray(k, v as Array<String>)
                v.isArrayOf<Int>() -> b.putIntArray(k, (v as Array<Int>).toIntArray())
                v.isArrayOf<Long>() -> b.putLongArray(k, (v as Array<Long>).toLongArray())
                else -> b.putString(k, v.joinToString(",")) // fallback
            }
            else -> b.putString(k, v.toString()) // fallback
        }
    }
    return b
}