package com.example.leo.myapplication.util

/**
 * https://github.com/twitter/elephant-bird/blob/master/core/src/main/java/com/twitter/elephantbird/util/StreamSearcher.java
 *
 * @author zhangzf
 * @create 2020/1/12 13:08
 */
class ByteArraySearcher(pattern_: ByteArray) {

    private val pattern = pattern_.copyOf()
    private val borders = IntArray(pattern.size + 1)

    init {
        preProcess()
    }

    fun contains(bytes: ByteArray) =
            indexOf(bytes) != -1

    fun indexOf(bytes: ByteArray): Int {
        var j = 0
        bytes.forEachIndexed { i, b ->
            while (j >= 0 && b != pattern[j]) j = borders[j]
            j++
            if (j == pattern.size) return i - j + 1
        }
        return -1
    }

    private fun preProcess() {
        var i = 0
        var j = -1
        borders[i] = j
        while (i < pattern.size) {
            while (j >= 0 && pattern[i] != pattern[j]) j = borders[j]
            borders[++i] = ++j
        }
    }
}
