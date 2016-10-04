package com.rsk.http.client

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.Listeners
import com.rsk.http.socket.ISocket

import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.gen5.api.Assertions
import org.junit.gen5.api.Assertions.*
import java.net.URL
import java.util.*

class HttpClientTaskSpek : Spek({
    val listeners: Listeners = Listeners(MultiplexWriter(), MultiplexOutputStream())
    val socket: ISocket = mock()
    val httpClient: IHttpClient = mock()
    val httpResponse: ProxyHttpResponse = mock()
    val proxyHttpEntity: ProxyHttpEntity = mock()

    beforeEach {
        reset(socket)

        whenever(httpResponse.statusLine).thenReturn("200 OK")
        whenever(httpResponse.entity).thenReturn(proxyHttpEntity)
        whenever(httpClient.executeCommand(URL("http://localhost"), "GET")).thenReturn(httpResponse)
        whenever(httpClient.executeCommand(URL("http://localhost:8080"), "GET")).thenReturn(httpResponse)
    }

    describe("the http client") {
        context("with the default port") {
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost HTTP/1.1")
            var listener: HttpClientTask? = null

            beforeEach {
                listener = ProxyHttpClientTask(connectionData, listeners, httpClient)
            }

            it("should set the port to the default value") {
                assertEquals(80, listener?.port)
            }
            it("should set the url to the given value") {
                assertEquals("localhost", listener?.serverUrl?.host)
            }
            it("should set the verb to 'GET'") {
                assertEquals("GET", listener?.verb)
            }
        }

        context("with a non standard port") {
            var listener: HttpClientTask? = null
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost:8080 HTTP/1.1")
            beforeEach {
                listener = ProxyHttpClientTask(connectionData, listeners, httpClient)
            }
            it("should set the port to the given value") {
                assertEquals(8080, listener?.port)
            }
        }

        it("should parse the request line correctly") {
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost HTTP/1.1")
            val proxyClient: ProxyHttpClientTask = ProxyHttpClientTask(connectionData, Listeners(MultiplexWriter(), MultiplexOutputStream()), httpClient)

            Assertions.assertEquals("GET", proxyClient.verb)
            Assertions.assertEquals(URL("http://localhost"), proxyClient.serverUrl)
            Assertions.assertEquals("HTTP/1.1", proxyClient.version)
            Assertions.assertEquals(80, proxyClient.port)
        }
    }
})