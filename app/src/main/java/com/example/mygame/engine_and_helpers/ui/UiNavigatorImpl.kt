package com.example.mygame.engine_and_helpers.ui

import android.app.Activity
import android.content.Intent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class UiNavigatorImpl @Inject constructor(
    private val activity: Activity
) : UiNavigator {

    override fun goTo(id: ViewId, args: Map<String, Any?>) {
        val intent = when (id) {
            ViewId.V1_ROOM_OR_TENT ->
                Intent(activity, com.example.mygame.views.FirstView::class.java)
            ViewId.V2_WORLD_MAP ->
                Intent(activity, com.example.mygame.views.SecondView::class.java)
            ViewId.V3_BATTLE_ROOM_OR_TENT ->
                Intent(activity, com.example.mygame.views.ThirdView::class.java)
            ViewId.V4_BATTLEFIELD_MAP ->
                Intent(activity, com.example.mygame.views.FourthView::class.java)
            ViewId.V5_DUEL ->
                Intent(activity, com.example.mygame.views.FifthView::class.java)
            ViewId.V6_BALL ->
                Intent(activity, com.example.mygame.views.SixthView::class.java)
            ViewId.V7_HUNT ->
                Intent(activity, com.example.mygame.views.SeventhView::class.java)
            ViewId.V8_FESTIVAL ->
                Intent(activity, com.example.mygame.views.EighthView::class.java)
            ViewId.V9_AUDIENCE ->
                Intent(activity, com.example.mygame.views.NinthView::class.java)
            ViewId.V10_VICTORY ->
                Intent(activity, com.example.mygame.views.TenthView::class.java)
            ViewId.V11_DEFEAT ->
                Intent(activity, com.example.mygame.views.EleventhView::class.java)
            ViewId.V12_MAIN_MENU ->
                Intent(activity, com.example.mygame.views.TwelfthView::class.java)
            ViewId.V13_TAVERN ->
                Intent(activity, com.example.mygame.views.ThirteenthView::class.java)
            ViewId.V14_JUDGE ->
                Intent(activity, com.example.mygame.views.FourteenthView::class.java)
            ViewId.V15_ARRESTED ->
                Intent(activity, com.example.mygame.views.FifteenthView::class.java)
            ViewId.V16_MEAL ->
                Intent(activity, com.example.mygame.views.SixteenthView::class.java)
        }

        // Pass args as extras (only primitives/parcelables/serializables work)
        for ((k, v) in args) {
            when (v) {
                is Int -> intent.putExtra(k, v)
                is Long -> intent.putExtra(k, v)
                is Boolean -> intent.putExtra(k, v)
                is String -> intent.putExtra(k, v)
                is java.io.Serializable -> intent.putExtra(k, v)
                is android.os.Parcelable -> intent.putExtra(k, v)
                // add more types as needed
            }
        }

        activity.startActivity(intent)
    }
}