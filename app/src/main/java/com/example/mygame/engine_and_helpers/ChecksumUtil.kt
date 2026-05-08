package com.example.mygame.engine_and_helpers

import java.security.MessageDigest

/** Hash helper used to seal commits against tampering */
object ChecksumUtil {
    fun sha256(bytes: ByteArray): String = MessageDigest
        .getInstance("SHA-256")
        .digest(bytes)
        .joinToString("") { "%02x".format(it) }
}