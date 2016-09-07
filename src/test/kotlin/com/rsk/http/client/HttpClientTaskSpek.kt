package com.rsk.http.client

import com.rsk.http.client.HttpClientTask
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.gen5.api.Assertions.*

class HttpClientTaskSpek : Spek({

    describe("a listener") {
        context("for a default port") {
            var listener: HttpClientTask = HttpClientTask("")

            beforeEach {
                listener = HttpClientTask("GET http://foo.bar HTTP/1.1")
            }

            it("should set the port to the default value") {
                assertEquals(listener.port, 80)
            }
            it("should set the url to the given value") {
                assertEquals(listener.serverUrl.host, "foo.bar")
            }
            it("should set the verb to 'GET'") {
                assertEquals(listener.verb, "GET")
            }
        }

        context("with a non standard port") {
            var listener: HttpClientTask = HttpClientTask("")
            beforeEach {
                listener = HttpClientTask("GET http://foo.bar:8080 HTTP/1.1")
            }
            it("should set the port to the given value") {

                assertEquals(listener.port, 8080)
            }
        }
    }
})