adb install -r --user 0 ../app/build/outputs/apk/release/app-release.apk

adb push hooks.json /data/local/tmp/

adb logcat -c
adb logcat -s ApiMonitor:* -e com.example.leo.monitor -f /mnt/sdcard/Download/123.log
adb logcat -s ApiMonitor:* -e com.example.leo.monitor | busybox tee 123.log

adb shell cmd -l
adb shell cmd package
adb shell cmd package list packages -3 -i -f --user 0
adb shell cmd activity force-stop de.robv.android.xposed.installer
adb shell cmd user list

adb shell pm list users
adb shell pm list packages -3 -i -f --user 0
adb shell pm path de.robv.android.xposed.installer
adb shell pm uninstall de.robv.android.xposed.installer

adb shell dumpsys -l
adb shell dumpsys package -h
adb shell dumpsys package packages
adb shell dumpsys package de.robv.android.xposed.installer
adb shell dumpsys activity -h
adb shell dumpsys activity processes
adb shell dumpsys user

adb shell settings put global policy_control immersive.full=*
adb shell settings put global policy_control null
