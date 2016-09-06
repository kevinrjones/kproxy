package com.rsk.http.socket

import com.rsk.http.socket.ISocket

interface IServerSocket {
    fun accept(): ISocket
}