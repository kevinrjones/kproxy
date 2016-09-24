package com.rsk.http.proxy

import com.rsk.http.socket.IServerSocket
import com.rsk.logger
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * The HttpMainProxyListener class will listen for incoming connections on {@link #_port port}
 *
 * It will create a <code>ServerSocket</code> for the user-agent to connect to. This is done by
 * the <code>start</code> method creating a thread to listen for incoming connections.
 * <p>
 *
 * @author Kevin Jones
 * @version 0.1
 * Created by kevin on 04/09/2016.
 */
class HttpMainProxyListener(val executor: ExecutorService,
                            val serverSocket: IServerSocket,
                            val proxyTaskFactory: IHttpProxyTaskFactory) : Thread() {

    val Logger by logger()
    var running = true

    override fun run() {
        synchronized(this)
        {
            do {
                Logger.debug("Waiting to connect")
                val acceptSocket = serverSocket.accept()
                Logger.debug("Accepted a connection on ${acceptSocket.inetAddress}:${acceptSocket.port}")

                // create ConnectionData and pass it off
                Logger.debug("Create a ConnectionData")
                val connectionData = ConnectionData(
                        acceptSocket,
                        "Active",
                        Date(),
                        acceptSocket.inetAddress.hostAddress
                )

                Logger.debug("Creating the server task")
                val serverTask = proxyTaskFactory.createServerTask(connectionData)
                executor.submit(serverTask)
            } while (running)
        }
    }

}

