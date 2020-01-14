adb install -r ../app/build/outputs/apk/release/app-release.apk

adb push hooks.json /data/local/tmp/

adb logcat -c
adb logcat -s ApiMonitor:* -e com.example.leo.myapplication -f /mnt/sdcard/Download/123.log
adb logcat -s ApiMonitor:* -e com.example.leo.myapplication | busybox tee 123.log
