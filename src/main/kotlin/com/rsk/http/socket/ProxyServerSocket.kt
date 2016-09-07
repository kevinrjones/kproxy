package com.rsk.http.socket

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

    override fun close() {
        ServerSocket.close()
    }

}