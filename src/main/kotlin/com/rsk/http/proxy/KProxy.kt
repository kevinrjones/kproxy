package com.rsk.http.proxy

import com.rsk.http.socket.NetServerSocket
import com.rsk.io.MultiplexWriter
import com.rsk.logger
import com.rsk.threading.ExceptionHandlingThreadPool
import java.io.OutputStreamWriter
import java.net.BindException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
The main console application code that will run and start the listener
 */
class KProxy() {

    var running = true
    val Logger by logger()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            System.setProperty("java.net.preferIPv4Stack", "true")

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

        val executor: ExecutorService = ExceptionHandlingThreadPool(10)

    }

    /**
     * @author Kevin Jones
     * @param port The http port to listen on
     */
    private fun start(port: Int) {
        var ss: NetServerSocket = NetServerSocket(port)
        val requestHeaderWriter = MultiplexWriter(OutputStreamWriter(System.out))
        val requestTypeWriter = MultiplexWriter(OutputStreamWriter(System.out))
        val responseHeaderWriter = MultiplexWriter(OutputStreamWriter(System.out))
        var responseTypeWriter = MultiplexWriter(OutputStreamWriter(System.out))


        do {
            try {
                Logger.debug("Starting proxy")

                Logger.debug("Create a new proxy listener")
                val server = HttpMainProxyListener(executor, ss, ProxyTaskFactory(Listeners(requestHeaderWriter, requestTypeWriter), Listeners(responseHeaderWriter, responseTypeWriter)))
                Logger.debug("Start the proxy listener")
                server.start()
                Logger.debug("Proxy started")
                server.join()
            } catch(ex: BindException) {
                println("Trying to bind on port $port and got an exception: ${ex.message}")
                return
            } catch (e: Exception) {
                try {
                    ss.close()
                    ss = NetServerSocket(port)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                    return
                }
                e.printStackTrace()
            }
        } while (running)
    }
}