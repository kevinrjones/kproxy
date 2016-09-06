package com.rsk.http.socket

import com.rsk.http.socket.ISocket
import java.net.InetAddress
import java.net.Socket

class ProxySocket(val socket: Socket) : ISocket {
    override val inetAddress: InetAddress
        get() = socket.inetAddress

}