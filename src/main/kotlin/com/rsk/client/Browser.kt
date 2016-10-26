package com.rsk.client

import com.sun.org.apache.xpath.internal.operations.Bool
import java.io.*
import java.net.Socket

/**
 * Created by kevin on 14/10/2016.
 */
class Program {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val browser = if (args[0] == "-p")
                Browser(args[1], Integer.parseInt(args[2]))
            else
                Browser(args[0], Integer.parseInt(args[1]))

            val reader = BufferedReader(InputStreamReader(browser.getHomePage()))

            var line = reader.readLine()
            var buffer: String = line + "\r\n"
            while (line.isNotEmpty()) {
                line = reader.readLine()
                buffer += line + "\r\n"
            }

            println(buffer)

            buffer = ""
            println("reading empty")
            while (line.isEmpty()) {
                line = reader.readLine()
                print(".")
            }

            while (line.isNotEmpty()) {
                line = reader.readLine()
                buffer += line + "\r\n"
            }

            println(buffer)
        }
    }
}

class Browser {
    val socket: Socket
    val useProxy: Boolean
    val forwardPort: Int

    constructor(address: String, port: Int, useProxy: Boolean = true) {
        var addr = address

        forwardPort = if (address.contains(':')) {
            addr = address.split(':')[0]
            Integer.parseInt(address.split(':')[1])
        } else {
            80
        }

        socket = Socket(addr, port)
        this.useProxy = useProxy
    }

    constructor(socket: Socket, useProxy: Boolean = true) {
        this.socket = socket
        this.useProxy = useProxy
        forwardPort = 80
    }

    fun getHomePage(): InputStream {
        val writer = BufferedWriter(OutputStreamWriter(socket.outputStream))

        if (useProxy) {
            writer.write("GET http://${socket.inetAddress.hostAddress}:$forwardPort/docs/cluster-howto.html HTTP/1.1\r\n")
        } else {
            writer.write("GET / HTTP/1.1\r\n")
        }
        writer.write("HOST: ${socket.inetAddress.hostAddress}\r\n")
        writer.write("Accept-Encoding: gzip, deflate\r\n")
        writer.write("\r\n")

        writer.flush()

        return socket.inputStream
    }
}

