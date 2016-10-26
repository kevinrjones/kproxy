package com.rsk.http.client

import com.rsk.http.socket.ISocket
import com.rsk.http.socket.NetSocket
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.client.methods.*
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.URL

class SocketHttpClient : IHttpClient {
    override fun createConnection(url: String, port: Int?): ISocket {
        val socket = Socket(url, port!!)
        return NetSocket(socket)
    }

}
