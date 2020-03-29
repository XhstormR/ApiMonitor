package com.example.leo.monitor.xposed.net

import java.io.InputStream

class MonitorInputStream(
    val host: String,
    val port: Int,
    private val delegate: InputStream
) : InputStream() {

    override fun read() =
        delegate.read()

    override fun read(b: ByteArray) =
        delegate.read(b)

    override fun read(b: ByteArray, off: Int, len: Int) =
        delegate.read(b, off, len)

    override fun skip(n: Long) =
        delegate.skip(n)

    override fun available() =
        delegate.available()

    override fun reset() =
        delegate.reset()

    override fun close() =
        delegate.close()

    override fun mark(readlimit: Int) =
        delegate.mark(readlimit)

    override fun markSupported() =
        delegate.markSupported()
}
