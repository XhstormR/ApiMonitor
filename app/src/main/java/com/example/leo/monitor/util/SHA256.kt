package com.example.leo.monitor.util

import java.io.File
import java.security.MessageDigest

object SHA256 {

    private const val ALGORITHM = "SHA-256"

    private val digestProvider = { MessageDigest.getInstance(ALGORITHM) }

    fun hash(input: File): String {
        val digest = digestProvider()

        input.inputStream().buffered().use {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val count = it.read(buffer)
                if (count == -1) break
                digest.update(buffer, 0, count)
            }
        }

        return digest.digest().toHEX()
    }

    fun hash(input: String): String {
        val digest = digestProvider()
        return digest.digest(input.toByteArray()).toHEX()
    }

    fun hash(input: ByteArray): String {
        val digest = digestProvider()
        return digest.digest(input).toHEX()
    }
}
