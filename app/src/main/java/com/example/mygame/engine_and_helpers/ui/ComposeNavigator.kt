package com.example.mygame.engine_and_helpers.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.ArrayDeque

data class Destination(val view: ViewId, val args: Map<String, Any?> = emptyMap())

class ComposeNavigator : UiNavigator {
    private val stack = ArrayDeque<Destination>()
    private val _current = MutableStateFlow(Destination(ViewId.V12_MAIN_MENU))
    val current: StateFlow<Destination> = _current

    override fun goTo(view: ViewId, args: Map<String, Any?>) {
        stack.addLast(Destination(view, args))
        _current.value = stack.peekLast()
    }

    fun back(): Boolean {
        if (stack.size <= 1) return false
        stack.removeLast()
        _current.value = stack.peekLast()
        return true
    }

    fun resetTo(view: ViewId, args: Map<String, Any?> = emptyMap()) {
        stack.clear()
        stack.addLast(Destination(view, args))
        _current.value = stack.peekLast()
    }
}