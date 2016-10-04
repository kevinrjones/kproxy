package com.rsk.io

import java.io.IOException
import java.io.Writer
import java.util.concurrent.CopyOnWriteArraySet

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
        containedWriters.forEach { it.write(c); it.flush() }
    }

    override fun write(cbuf: CharArray) {
        containedWriters.forEach { it.write(cbuf); it.flush() }
    }

    override fun write(str: String) {
        containedWriters.forEach { it.write(str); it.flush() }
    }

    override fun write(str: String, off: Int, len: Int) {
        containedWriters.forEach { it.write(str, off, len); it.flush() }
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