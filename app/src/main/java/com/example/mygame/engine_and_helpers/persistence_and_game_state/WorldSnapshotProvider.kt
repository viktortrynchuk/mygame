package com.example.mygame.engine_and_helpers.persistence_and_game_state

/** Provides an opaque snapshot of world state for commit hashing. */
interface WorldSnapshotProvider { fun snapshot(): ByteArray }