package com.rsk.io

import java.io.IOException
import java.io.OutputStream
import java.io.Writer
import java.util.concurrent.CopyOnWriteArraySet

class MultiplexOutputStream() : OutputStream() {
    private val containedStreams = CopyOnWriteArraySet<OutputStream>()

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

class MultiplexWriter() : Writer() {
    private val containedWriters = CopyOnWriteArraySet<Writer>()

    constructor(writer1: Writer) : this() {
        add(writer1)
    }

    constructor(writer1: Writer, writer2: Writer) : this() {
        add(writer1)
        add(writer2)
    }

    fun add(writer: Writer): Boolean {
        return this.containedWriters.add(writer)
    }

    @Throws(IOException::class)
    override fun close() {
        containedWriters.forEach { it.close() }
    }

    @Throws(IOException::class)
    override fun flush() {
        containedWriters.forEach {
            it.flush()
        }

    }

    fun remove(writer: Writer): Boolean {
        return containedWriters.remove(writer)
    }


    override fun write(cbuf: CharArray, off: Int, len: Int) {
        containedWriters.forEach {
            it.write(cbuf, off, len)
        }
    }

    override fun write(c: Int) {
        containedWriters.forEach { it.write(c) }
    }

    override fun write(cbuf: CharArray) {
        containedWriters.forEach { it.write(cbuf) }
    }

    override fun write(str: String) {
        containedWriters.forEach { it.write(str) }
    }

    override fun write(str: String, off: Int, len: Int) {
        containedWriters.forEach { it.write(str, off, len) }
    }

    override fun append(c: Char): Writer {
        containedWriters.forEach { it.append(c) }
        return this
    }

    override fun append(csq: CharSequence?): Writer {
        containedWriters.forEach { it.append(csq) }
        return this
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): Writer {
        containedWriters.forEach { it.append(csq, start, end) }
        return this
    }
}