package com.rsk.http.server

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.gen5.api.Assertions.*

class HttpListenerSpek : Spek({
    val listener = HttpListener("GET http://foo.bar HTTP/1.1")

    describe("a listener") {
        it("set the port") {
            assertEquals(listener.port, 80)
        }
    }
})