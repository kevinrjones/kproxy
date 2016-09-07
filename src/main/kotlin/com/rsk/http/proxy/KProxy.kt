package com.rsk.http.proxy

import com.rsk.http.server.ProxyServerTaskFactory
import com.rsk.http.socket.ProxyServerSocket

/**
The main console application code that will run and start the listener
 */
class KProxy() {

    var running = true

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val proxy = KProxy()

            var port: Int = 8080
            var i = 0
            while (i < args.size) {
                when (args[i]) {
                    "-port" -> port = Integer.parseInt(args[++i])
                }
                i++
            }

            proxy.start(port)
        }
    }

    /**
     * @author Kevin Jones
     * @param port The http port to listen on
     */
    private fun start(port: Int) {
        var ss: ProxyServerSocket = ProxyServerSocket(port)

        do {
            try {
                val server = HttpMainProxyListener(ProxyServerSocket(port), ProxyServerTaskFactory())
                server.start()
            } catch (e: Exception) {
                try {
                    ss.close()
                    ss = ProxyServerSocket(port)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                    return
                }
                e.printStackTrace()
            }
        } while (running)
    }
}