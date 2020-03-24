package com.example.leo.myapplication

import com.example.leo.myapplication.util.ByteArraySearcher
import com.example.leo.myapplication.util.indexOf
import com.example.leo.myapplication.util.putIfAbsent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author zhangzf
 * @create 2020/1/12 12:52
 */
fun main() {
    val s = byteArrayOf(123, -106, -110, -35, 23, -113, -30, -126, -75, 50, 114, -118, -2, 70, -80, -37, -18, 28, 126, 105)
    val p1 = byteArrayOf(-126, -75, 50, 114, -118, -2, 70)
    val p2 = byteArrayOf(126, 105)
    val p3 = byteArrayOf(126, 105, 1)
    val p4 = byteArrayOf(28, 126, 105)
    val p5 = byteArrayOf(28, 126, 105, 1)
    println(s.indexOf(p1) == ByteArraySearcher(p1).indexOf(s))
    println(s.indexOf(p2) == ByteArraySearcher(p2).indexOf(s))
    println(s.indexOf(p3) == ByteArraySearcher(p3).indexOf(s))
    println(s.indexOf(p4) == ByteArraySearcher(p4).indexOf(s))
    println(s.indexOf(p5) == ByteArraySearcher(p5).indexOf(s))

    val set = ConcurrentHashMap.newKeySet<Int>()
    val int = AtomicInteger()
    val executorService = Executors.newCachedThreadPool()
    repeat(10) {
        executorService.execute {
            repeat(1000) {
                if (set.putIfAbsent(it)) {
                    int.getAndIncrement()
                }
            }
        }
    }
    executorService.shutdown()
    executorService.awaitTermination(1, TimeUnit.DAYS)
    println(int.get() == 1000)
}
