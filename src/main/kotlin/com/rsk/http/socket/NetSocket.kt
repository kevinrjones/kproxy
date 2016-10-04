package com.rsk.http.socket

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket

class NetSocket(val socket: Socket) : ISocket {

    override fun close() {
        socket.close()
    }

    override val inputStream: InputStream
        get() = socket.inputStream
    override val outputStream: OutputStream
        get() = socket.outputStream
    override val inetAddress: InetAddress
        get() = socket.inetAddress
    override val port: Int
        get() = socket.port
}