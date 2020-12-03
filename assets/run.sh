#!/bin/sh
# place this file at /data/adb/service.d
# https://github.com/topjohnwu/Magisk/blob/master/docs/guides.md#boot-scripts

sleep 60
am start --user 19 com.example.leo.monitor
