package com.rsk.http.server

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.gen5.api.Assertions.*

class HttpListenerSpek : Spek({

    val listener = HttpListener("GET http://foo.bar HTTP/1.1")
    describe("a listener") {
        it("should set the port to the default value") {
            assertEquals(listener.port, 80)
        }
        it("should set the url to the given value") {
            assertEquals(listener.serverUrl.host, "foo.bar")
        }
        it("should set the verb to 'GET'") {
            assertEquals(listener.verb, "GET")
        }

        describe("with a non standard port") {
            val listener = HttpListener("GET http://foo.bar:8080 HTTP/1.1")
            it("should set the port to the given value") {

                assertEquals(listener.port, 8080)
            }
        }
    }
})