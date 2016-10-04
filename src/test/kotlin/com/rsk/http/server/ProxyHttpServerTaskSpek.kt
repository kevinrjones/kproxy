package com.rsk.http.server

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.IHttpProxyTaskFactory
import com.rsk.http.proxy.Listeners
import com.rsk.http.socket.ISocket
import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.gen5.api.Assertions
import java.io.ByteArrayInputStream
import java.util.*

class ProxyHttpServerTaskSpek : Spek({

    val socket: ISocket = mock()
    var serverTask:ProxyHttpServerTask? = null
    val httpProxyTaskFactory: IHttpProxyTaskFactory = mock()
    val listeners: Listeners = Listeners(MultiplexWriter(), MultiplexOutputStream())

    describe("the proxy server task") {

        beforeEach {
            val data: String = "GET http://host.com HTTP/1.1\r\nHost: host.com\r\nContent-length: 22\r\n"
            whenever(socket.inputStream).thenReturn(ByteArrayInputStream(data.toByteArray()))

            val connectionData = ConnectionData(socket, "active", Date(), "host")
            serverTask = ProxyHttpServerTask(connectionData, httpProxyTaskFactory, listeners)
            serverTask?.running = false
        }

        it("should set the request line") {
            serverTask?.run()
            Assertions.assertEquals("GET http://host.com HTTP/1.1", serverTask?.clientData?.strRequestLine)
        }

        it("should set the headers") {
            serverTask?.run()
            Assertions.assertEquals(2, serverTask?.clientData?.headers?.size)
        }
    }
})