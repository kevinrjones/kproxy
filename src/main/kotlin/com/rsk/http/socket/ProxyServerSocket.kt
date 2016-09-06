package com.rsk.http.socket

import com.rsk.http.socket.ProxySocket
import com.rsk.http.socket.IServerSocket
import com.rsk.http.socket.ISocket
import java.net.ServerSocket

class ProxyServerSocket : IServerSocket {

    var ServerSocket: ServerSocket

    constructor(port: Int) {
        ServerSocket = ServerSocket(port)
    }

    override fun accept(): ISocket {
        val socket = ServerSocket.accept()
        return ProxySocket(socket)
    }
}