package com.example.leo.monitor

import com.topjohnwu.superuser.Shell
import java.io.File

object ExecutorService {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.Builder.create()
            .setFlags(Shell.FLAG_REDIRECT_STDERR)
            .setTimeout(10)
            .let { Shell.setDefaultBuilder(it) }
    }

    private const val PROCESS_KILL =
        """kill -9 %s"""

    private const val ACTIVITY_START =
        """am start --user %d %s"""
    private const val ACTIVITY_STOP =
        """am force-stop --user %d %s"""
    private const val ACTIVITY_TOP =
        """dumpsys activity activities | grep mFocusedActivity | cut -d' ' -f 6"""

    private const val PACKAGE_REVOKE_PERMISSION =
        """pm revoke --user %d %s %s"""
    private const val PACKAGE_INSTALL =
        """pm install -r --user %d %s"""
    private const val PACKAGE_UNINSTALL =
        """pm uninstall --user %d %s"""

    private const val FILE_BACKUP =
        """cp -a %s %s"""
    private const val FILE_REMOVE =
        """rm -rf %s"""

    private const val MONKEY_TEST =
        """monkey --kill-process-after-error --pct-touch 100 --throttle 500 500"""

    private const val FRIDA_SERVER_START =
        """/data/local/tmp/frida-server -D -l 0.0.0.0"""
    private const val FRIDA_SERVER_PID =
        """pidof frida-server"""

    fun killProcess(pid: Int) =
        execute(PROCESS_KILL.format(pid))

    fun startActivity(userId: Int, packageName: String) =
        execute(ACTIVITY_START.format(userId, packageName))

    fun stopActivity(userId: Int, packageName: String) =
        execute(ACTIVITY_STOP.format(userId, packageName))

    fun revokePackagePermission(userId: Int, packageName: String, permission: String) =
        execute(PACKAGE_REVOKE_PERMISSION.format(userId, packageName, permission))

    fun installPackage(userId: Int, packagePath: File) =
        execute(PACKAGE_INSTALL.format(userId, packagePath))

    fun uninstallPackage(userId: Int, packageName: String) =
        execute(PACKAGE_UNINSTALL.format(userId, packageName))

    fun backupFile(path: String, packageName: String) =
        execute(FILE_BACKUP.format(path, packageName))

    fun removeFile(path: String) =
        execute(FILE_REMOVE.format(path))

    fun getTopComponent() =
        execute(ACTIVITY_TOP).out.joinToString()

    fun monkeyTest() =
        execute(MONKEY_TEST)

    fun startFrida() {
        if (!isFridaRunning()) execute(FRIDA_SERVER_START)
    }

    fun stopFrida() {
        if (isFridaRunning()) killProcess(getFridaPID()!!)
    }

    private fun isFridaRunning() = getFridaPID() != null

    private fun getFridaPID() = runCatching { execute(FRIDA_SERVER_PID).out.singleOrNull()?.toInt() }.getOrNull()

    private fun execute(command: String) =
        Shell.su(command).exec()
            .also { require(it.isSuccess) { it.err.joinToString(System.lineSeparator()) } }
}
