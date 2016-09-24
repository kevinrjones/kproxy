package com.rsk.http.socket

import java.net.ServerSocket

class NetServerSocket : IServerSocket {
    var ServerSocket: ServerSocket

    constructor(port: Int) {
        ServerSocket = ServerSocket(port)
    }

    override fun accept(): ISocket {
        val socket = ServerSocket.accept()
        return NetSocket(socket)
    }

    override fun close() {
        ServerSocket.close()
    }

}