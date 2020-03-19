package com.example.leo.myapplication

import com.example.leo.myapplication.model.parcel.ProcessResult

object ExecutorService {

    private const val SU = "su -c %s"

    private const val PROCESS_LIST = "ps %s"
    private const val PROCESS_KILL = "kill -9 %s"

    private const val ACTIVITY_STOP = "am force-stop %s"

    private const val PACKAGE_PATH = "pm path %s"
    private const val PACKAGE_REVOKE_PERMISSION = "pm revoke %s %s"
    private const val PACKAGE_INSTALL = "pm install -r %s"
    private const val PACKAGE_UNINSTALL = "pm uninstall %s"

    private const val FILE_BACKUP = "cp -a %s %s"
    private const val FILE_REMOVE = "rm -rf %s"

    private const val MONKEY_TEST = "monkey --kill-process-after-error --pct-touch 100 --throttle 500 -p %s 500"

    private val processBuilder = ProcessBuilder()
        .redirectErrorStream(true)

    fun listProcess(pid: Int?) =
        if (pid == null) execute(PROCESS_LIST)
        else execute(PROCESS_LIST.format(pid))

    fun killProcess(pid: Int) =
        execute(PROCESS_KILL.format(pid))

    fun stopActivity(packageName: String) =
        execute(ACTIVITY_STOP.format(packageName))

    fun pathPackage(packageName: String) =
        execute(PACKAGE_PATH.format(packageName))
            .let { it.copy(content = it.content.trim().removePrefix("package:")) }

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

    private fun execute(command: String): ProcessResult {
        val process = SU.format(command).runCommand()
        val exitValue = process.waitFor()
        val content = process.inputStream.bufferedReader().use { it.readText() }
        return ProcessResult(exitValue, content)
    }

    private fun String.runCommand() =
        processBuilder.command(split(" ")).start()
}
