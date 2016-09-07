package com.rsk.http.socket


interface IServerSocket {
    fun accept(): ISocket

    fun close()
}