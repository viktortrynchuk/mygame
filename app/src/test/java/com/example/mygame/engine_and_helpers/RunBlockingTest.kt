package com.example.mygame.engine_and_helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun <T> runBlockingTest(block: suspend () -> T): T = runBlocking {
    withContext(Dispatchers.Unconfined) { block() }
}
