package com.rsk.http.server

import com.nhaarman.mockito_kotlin.*
import com.rsk.http.client.HttpClientTask
import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.IHttpProxyTaskFactory
import com.rsk.http.proxy.Listeners
import com.rsk.http.socket.ISocket
import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.util.*

class ProxyHttpServerTaskSpek : Spek({

    val socket: ISocket = mock()
    var serverTask:ProxyHttpServerTask? = null
    val clientTask: HttpClientTask = mock()
    val httpProxyTaskFactory: IHttpProxyTaskFactory = mock()
    val listeners: Listeners = Listeners(MultiplexWriter(), MultiplexOutputStream())
    val data: String = "GET http://host.com HTTP/1.1\r\nHost: host.com\r\nContent-length: 22\r\n\r\n1234567890123456789012"

    describe("the proxy server task") {

        beforeEach {
            whenever(socket.inputStream).thenReturn(ByteArrayInputStream(data.toByteArray()))

            val connectionData = ConnectionData(socket, "active", Date(), "host")
            whenever(httpProxyTaskFactory.createClientTask(any(), any())).thenReturn(clientTask)
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

        context("when processing a request") {
            val outputStream: OutputStream = mock()
            val clientTask: HttpClientTask = mock()

            beforeEach {
                reset(socket)
                reset(clientTask)


                whenever(httpProxyTaskFactory.createClientTask(any(), any())).thenReturn(clientTask)
                whenever(socket.inputStream).thenReturn(ByteArrayInputStream(data.toByteArray()))
                whenever(socket.outputStream).thenReturn(outputStream)

                serverTask?.run()
            }

            it("should send two headers to the origin server") {
                verify(clientTask, times(2)).writeHeader(any())
            }

            it("should send the content to the origin server") {
                verify(clientTask).writeEntity("1234567890123456789012".toByteArray(), outputStream)
            }
        }

    }
})