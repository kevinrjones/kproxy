package com.rsk.http.client

import com.rsk.http.server.HttpTask
import com.rsk.http.socket.ISocket
import com.rsk.io.MultiplexWriter
import java.io.OutputStream
import java.net.URI
import java.net.URL

interface HttpClientTask  : HttpTask {
    var verb: String
    var serverUrl: URL
    var version: String
    var port: Int?
    var entityUrl: String

    var serverSocket: ISocket

    fun startConnection()
    fun processResponse()
}