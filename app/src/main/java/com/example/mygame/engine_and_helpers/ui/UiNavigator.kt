package com.example.mygame.engine_and_helpers.ui

interface UiNavigator {
    fun goTo(view: ViewId, args: Map<String, Any?> = emptyMap())
}