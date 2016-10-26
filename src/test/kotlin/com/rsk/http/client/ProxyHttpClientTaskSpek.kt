package com.rsk.http.client

import com.nhaarman.mockito_kotlin.*
import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.Listeners
import com.rsk.http.server.HttpServerTask
import com.rsk.http.socket.ISocket

import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.*

class ProxyHttpClientTaskSpek : Spek({
    val listeners: Listeners = Listeners(MultiplexWriter(), MultiplexOutputStream())
    val socket: ISocket = mock()
    val httpClient: IHttpClient = mock()
    val httpResponse: ProxyHttpResponse = mock()
    val proxyHttpEntity: ProxyHttpEntity = mock()
    val server: HttpServerTask = mock()
    val outputStream: OutputStream = mock()

    beforeEach {
        reset(socket)
        reset(outputStream)

        whenever(httpResponse.statusLine).thenReturn("200 OK")
        whenever(httpResponse.entity).thenReturn(proxyHttpEntity)
        whenever(httpClient.createConnection("localhost", 80)).thenReturn(socket)
        whenever(httpClient.createConnection("localhost", 8080)).thenReturn(socket)
        whenever(socket.outputStream).thenReturn(outputStream)
    }

    describe("the http client") {
        on("with the default port") {
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost HTTP/1.1")
            var listener: HttpClientTask? = null

            beforeEach {
                listener = ProxyHttpClientTask(connectionData, listeners, httpClient, server)
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

        on("with a non standard port") {
            var listener: HttpClientTask? = null
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost:8080 HTTP/1.1")
            beforeEach {
                listener = ProxyHttpClientTask(connectionData, listeners, httpClient, server)
            }
            it("should set the port to the given value") {
                assertEquals(8080, listener?.port)
            }
        }

        it("should parse the request line correctly") {
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost HTTP/1.1")
            val proxyClient: ProxyHttpClientTask = ProxyHttpClientTask(connectionData, Listeners(MultiplexWriter(), MultiplexOutputStream()), httpClient, server)

            Assertions.assertEquals("GET", proxyClient.verb)
            Assertions.assertEquals(URL("http://localhost"), proxyClient.serverUrl)
            Assertions.assertEquals("HTTP/1.1", proxyClient.version)
            Assertions.assertEquals(80, proxyClient.port)
        }

        it("should not write the the request line if the request is a CONNECT") {
            val connectionData = ConnectionData(socket, "active", Date(), "", "CONNECT http://localhost HTTP/1.1")
            ProxyHttpClientTask(connectionData, Listeners(MultiplexWriter(), MultiplexOutputStream()), httpClient, server)

            verify(outputStream, times(0)).write(any<ByteArray>())
        }

        it("should not write the the request line if the request is not a CONNECT") {
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost HTTP/1.1")
            ProxyHttpClientTask(connectionData, Listeners(MultiplexWriter(), MultiplexOutputStream()), httpClient, server)

            verify(outputStream, times(1)).write(any<ByteArray>())

        }

        context("when processing a response") {
            var listener: HttpClientTask? = null
            val connectionData = ConnectionData(socket, "active", Date(), "", "GET http://localhost:8080 HTTP/1.1")
            var stream: InputStream

            beforeEach {
                listener = ProxyHttpClientTask(connectionData, listeners, httpClient, server)

            }

            it("should write the response line to the client") {
                stream = "HTTP/1.1 200 OK\r\n".byteInputStream()
                whenever(socket.inputStream).thenReturn(stream)
                listener?.processResponse()

                verify(server, times(1)).writeHeader("HTTP/1.1 200 OK")
                verify(server, times(1)).writeHeader("\r\n")
            }

            it("should write the response headers to the client") {
                stream = "HTTP/1.1 200 OK\r\nfoo: bar\r\nbaz: bux\r\n\r\n".byteInputStream()
                whenever(socket.inputStream).thenReturn(stream)
                listener?.processResponse()

                verify(server, times(1)).writeHeader("foo: bar")
                verify(server, times(1)).writeHeader("baz: bux")
            }

            it("should write the response entity to the client") {
                stream = "HTTP/1.1 200 OK\r\nContent-Length: 10\r\n\r\n1234567890".byteInputStream()
                whenever(socket.inputStream).thenReturn(stream)
                listener?.processResponse()

                verify(server, times(1)).writeEntity("1234567890".toByteArray(), socket.outputStream)
            }
        }

        context("when processing a request") {
            val connectionData = ConnectionData(socket, "active", Date(), "host", "GET http://server.com HTTP/1.1")
            val httpClient: IHttpClient = mock()
            val socket: ISocket = mock()
            val listeners: Listeners = Listeners(MultiplexWriter(), MultiplexOutputStream())
            var baos:ByteArrayOutputStream = ByteArrayOutputStream()

            beforeEach {

                baos = ByteArrayOutputStream()
                whenever(httpClient.createConnection("server.com", 80)).thenReturn(socket)
                whenever(socket.outputStream).thenReturn(baos)
            }

            it("should write the a header to the server") {
                val client = ProxyHttpClientTask(connectionData, listeners, httpClient, mock())


                client.writeHeader("12345")

                Assertions.assertArrayEquals(baos.toByteArray(), "12345".toByteArray())
            }


            it("should write the response entity to the server") {
                val client = ProxyHttpClientTask(connectionData, listeners, httpClient, mock())


                client.writeEntity("12345".toByteArray(), baos)

                Assertions.assertArrayEquals(baos.toByteArray(), "12345".toByteArray())
            }
        }
    }
})