#!/bin/sh
# place this file at /data/adb/service.d
# https://github.com/topjohnwu/Magisk/blob/master/docs/guides.md#boot-scripts
# adb push run.sh /data/local/tmp/run.sh && adb shell "su -c 'mv /data/local/tmp/run.sh /data/adb/service.d/run.sh && chmod +x /data/adb/service.d/run.sh'"

sleep 60
am start --user 19 com.example.leo.monitor
am start -a android.intent.action.MAIN -c android.intent.category.HOME
