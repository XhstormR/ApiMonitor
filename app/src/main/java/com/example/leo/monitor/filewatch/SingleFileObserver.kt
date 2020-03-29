package com.example.leo.monitor.filewatch

import android.os.FileObserver
import com.example.leo.monitor.util.Logger
import java.io.File
import java.util.Locale

data class SingleFileObserver(private val parent: File) : FileObserver(parent.path) {

    companion object {
        private const val MASK_DIR = 0x40000000
    }

    override fun onEvent(event: Int, path: String?) {
        path ?: return

        val action = event and ALL_EVENTS

        val isDir = event and MASK_DIR != 0

        val file = parent.resolve(path)

        if (isDir) {
            when (action) {
                CREATE -> FileWatcher.DIRS.add(SingleFileObserver(file).apply { startWatching() })
                MOVED_TO -> FileWatcher.DIRS.add(SingleFileObserver(file).apply { startWatching() })
                MOVED_FROM -> FileWatcher.DIRS.remove(SingleFileObserver(file))
                DELETE, DELETE_SELF -> FileWatcher.DIRS.remove(SingleFileObserver(file))
            }
        } else {
            when (action) {
                CREATE -> {
                    if (file.extension.toLowerCase(Locale.ROOT) == "apk") {
                        Logger.logError(file)
                    }
                }
            }
        }
    }
}

/*
https://github.com/torvalds/linux/blob/master/include/uapi/linux/inotify.h
*/
