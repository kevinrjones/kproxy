package com.rsk.http.com.rsk.http.proxy

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.rsk.http.proxy.HttpMainProxyListener
import com.rsk.http.server.HttpServerTask
import com.rsk.http.server.HttpServerTaskFactory
import com.rsk.http.socket.IServerSocket
import com.rsk.http.socket.ISocket
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.InetAddress


/**
 *
 */
class MainHttpProxyListenerSpek: Spek( {

    var socket: ISocket = mock()
    var serverTaskFactory: HttpServerTaskFactory = mock()

    var httpServerTask: HttpServerTask?= null
    var serverSocket: IServerSocket? = null
    var listener: HttpMainProxyListener? = null

    describe("the proxy") {
        beforeEach {
            httpServerTask = mock()
            serverSocket = mock()

            whenever(serverSocket?.accept()).thenReturn(socket)
            whenever(socket.inetAddress).thenReturn(InetAddress.getLocalHost())
            whenever(serverTaskFactory.createServerTask(any())).thenReturn(httpServerTask)

            listener = HttpMainProxyListener(serverSocket!!, serverTaskFactory)
        }

        it("should accept the server socket"){
            listener?.running = false
            listener?.run()

            verify(serverSocket)?.accept()

        }

        it("should start the server task"){
            listener?.running = false
            listener?.run()

            verify(httpServerTask)?.start()

        }

    }
})