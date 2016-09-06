package com.rsk.http.proxy

import com.rsk.http.server.HttpServerTask
import com.rsk.http.socket.IServerSocket
import com.rsk.logger
import java.net.ServerSocket
import java.util.*

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
class HttpMainProxyListener(val listenPort: Int, val serverSocket: IServerSocket) : Thread() {

    val Logger by logger()

    override fun run() {
        synchronized(this)
        {

            while(true) {
                val acceptSocket = serverSocket.accept()
                Logger.debug("Accepted a connection $acceptSocket")

                // create ConnectionData and pass it off
                var connectionData = ConnectionData(
                        acceptSocket,
                        "Active",
                        Date(),
                        acceptSocket.inetAddress.hostAddress)

                // start server thread to listen to connection
                var serverTask = HttpServerTask(connectionData)
                serverTask.start()
            }

        }
    }
}
