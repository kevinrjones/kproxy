package com.rsk.http.server.integration

import com.nhaarman.mockito_kotlin.mock
import com.rsk.http.client.HttpClientTask
import com.rsk.http.proxy.HttpMainProxyListener
import com.rsk.http.proxy.IHttpProxyTaskFactory
import com.rsk.http.socket.IServerSocket
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.concurrent.ExecutorService

/**
 * Created by kevinj on 03/09/2016.
 */
class HttpListenerSpek : Spek ({

    val executorService: ExecutorService = mock()
    val serverSocket: IServerSocket = mock()
    val httpProxyTaskFactory: IHttpProxyTaskFactory = mock()

    val listener = HttpMainProxyListener(executorService, serverSocket, httpProxyTaskFactory)

    describe("the http listener") {
        it("should connect to the server") {

            // todo: test
            //listener.run()
        }
    }
})

