package com.rsk.http.proxy

import com.nhaarman.mockito_kotlin.*
import com.rsk.http.server.HttpServerTask
import com.rsk.http.socket.IServerSocket
import com.rsk.http.socket.ISocket
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.InetAddress
import java.util.concurrent.ExecutorService


/**
 *
 */
class MainHttpProxyListenerSpek : Spek({

    val socket: ISocket = mock()
    val serverTaskFactory: IHttpProxyTaskFactory = mock()
    val httpServerTask: HttpServerTask = mock()
    val serverSocket: IServerSocket = mock()
    val executorService: ExecutorService = mock()

    describe("the main proxy listener") {
        beforeEach {

            reset(executorService)
            reset(serverSocket)
            reset(httpServerTask)

            whenever(serverSocket.accept()).thenReturn(socket)
            whenever(socket.inetAddress).thenReturn(InetAddress.getLocalHost())
            whenever(serverTaskFactory.createServerTask(any())).thenReturn(httpServerTask)


            val listener = HttpMainProxyListener(executorService, serverSocket, serverTaskFactory )

            listener.running = false
            listener.run()
        }

        it("should accept the server socket") {
            verify(serverSocket).accept()
        }

        it("should submit the server task") {
            verify(executorService).submit(any())
        }
    }
})