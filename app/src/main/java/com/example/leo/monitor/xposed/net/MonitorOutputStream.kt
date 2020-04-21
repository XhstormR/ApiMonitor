package com.example.leo.monitor.xposed.net

import java.io.OutputStream

class MonitorOutputStream(
    val hostAddress: String,
    val hostName: String,
    val port: Int,
    private val delegate: OutputStream
) : OutputStream() {

    override fun write(b: Int) =
        delegate.write(b)

    override fun write(b: ByteArray) =
        delegate.write(b)

    override fun write(b: ByteArray, off: Int, len: Int) =
        delegate.write(b, off, len)

    override fun flush() =
        delegate.flush()

    override fun close() =
        delegate.close()
}
