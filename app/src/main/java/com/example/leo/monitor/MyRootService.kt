package com.example.leo.monitor

import android.app.ActivityManager
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.core.os.bundleOf
import com.example.leo.monitor.util.Logger
import com.example.leo.monitor.util.clazz
import com.topjohnwu.superuser.ipc.RootService

class MyRootService : RootService(), Handler.Callback {

    override fun onBind(intent: Intent): IBinder {
        Logger.logError("MyRootService: onBind")
        val messenger = Messenger(Handler(Looper.getMainLooper(), this))
        return messenger.binder
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            Const.MSG_DO_FOREGROUND -> {
                val packageName = msg.data[Const.KEY_DO_FOREGROUND].toString()
                val activityManager = getSystemService(clazz<ActivityManager>())
                val isTop = activityManager.runningAppProcesses
                    .filter { it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
                    .any { it.processName == packageName }
                val reply = Message.obtain(msg).apply {
                    data = bundleOf(Const.KEY_DO_FOREGROUND to if (isTop) null else packageName)
                }
                msg.replyTo.send(reply)
            }
        }
        return false
    }
}
