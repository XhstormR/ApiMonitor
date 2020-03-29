package com.example.leo.monitor.filewatch

import android.os.Environment
import java.util.concurrent.ConcurrentHashMap

object FileWatcher {

    val DIRS = ConcurrentHashMap.newKeySet<SingleFileObserver>()

    fun start() {
        stop()

        Environment.getExternalStorageDirectory()
            .walk()
            .filter { it.isDirectory }
            .map { SingleFileObserver(it) }
            .let { DIRS.addAll(it) }

        DIRS.forEach { it.startWatching() }
    }

    fun stop() {
        DIRS.clear()
    }
}
