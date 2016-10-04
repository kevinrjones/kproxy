package com.rsk.io

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.StringWriter
import org.junit.gen5.api.Assertions.*

class MultiplexWriterSpek : Spek( {
    describe("a multiplex writer") {
        it("should write to a write when there is only one writer") {
            val output = StringWriter()
            val multiplex = MultiplexWriter(output)

            multiplex.write("Hello World")

            assertEquals("Hello World", output.buffer.toString());
        }
    }
})