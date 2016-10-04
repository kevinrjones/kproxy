package com.rsk.io

import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.CopyOnWriteArraySet

class MultiplexOutputStream() : OutputStream() {
    private val containedStreams = CopyOnWriteArraySet<OutputStream>()

    constructor(os1: OutputStream) : this() {
        add(os1)
    }
    constructor(os1: OutputStream, os2: OutputStream) : this() {
        add(os1)
        add(os2)
    }

    fun add(os: OutputStream): Boolean {
        return this.containedStreams.add(os)
    }

    @Throws(IOException::class)
    override fun close() {
        containedStreams.forEach { it.close() }
    }

    @Throws(IOException::class)
    override fun flush() {
        containedStreams.forEach { it.flush() }
    }

    fun remove(os: OutputStream): Boolean {
        return this.containedStreams.remove(os)
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        containedStreams.forEach { it.write(b) }
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray) {
        containedStreams.forEach { it.write(b) }

    }

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        containedStreams.forEach { it.write(b, off, len) }

    }
}

