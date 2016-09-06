package com.rsk.http.proxy

import com.rsk.http.proxy.HttpMainProxyListener


/**
 The main console application code that will run and start the listener
 */


class KProxy(){

    companion object {
        @JvmStatic
        fun main (args:Array<String>) {
            val proxy = KProxy()

            var port:Int = 8080
            var i = 0;
            while (i < args.size) {
                when (args[i]) {
                    "-port" -> port = Integer.parseInt(args[++i])
                }
                i++
            }

            proxy.start(port);
        }
    }

    private fun start(port: Int) {
        val server = HttpMainProxyListener(port)
        server.start()
    }
}