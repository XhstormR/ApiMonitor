package com.example.leo.monitor

import com.topjohnwu.superuser.Shell

object ExecutorService {

    init {
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
        Shell.Config.verboseLogging(BuildConfig.DEBUG)
        Shell.Config.setTimeout(10)
    }

    private const val PROCESS_KILL = "kill -9 %s"

    private const val ACTIVITY_STOP = "am force-stop %s"

    private const val PACKAGE_REVOKE_PERMISSION = "pm revoke %s %s"
    private const val PACKAGE_INSTALL = "pm install -r %s"
    private const val PACKAGE_UNINSTALL = "pm uninstall %s"

    private const val FILE_BACKUP = "cp -a %s %s"
    private const val FILE_REMOVE = "rm -rf %s"

    private const val MONKEY_TEST = "monkey --kill-process-after-error --pct-touch 100 --throttle 500 -p %s 500"

    fun killProcess(pid: Int) =
        execute(PROCESS_KILL.format(pid))

    fun stopActivity(packageName: String) =
        execute(ACTIVITY_STOP.format(packageName))

    fun revokePackagePermission(packageName: String, permission: String) =
        execute(PACKAGE_REVOKE_PERMISSION.format(packageName, permission))

    fun installPackage(path: String) =
        execute(PACKAGE_INSTALL.format(path))

    fun uninstallPackage(packageName: String) =
        execute(PACKAGE_UNINSTALL.format(packageName))

    fun backupFile(path: String, packageName: String) =
        execute(FILE_BACKUP.format(path, packageName))

    fun removeFile(path: String) =
        execute(FILE_REMOVE.format(path))

    fun monkeyTest(packageName: String) =
        execute(MONKEY_TEST.format(packageName))

    private fun execute(command: String) =
        Shell.su(command).exec()
            .also { require(it.code == 0) { it.out.joinToString(System.lineSeparator()) } }
}
