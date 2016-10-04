package com.rsk.http.server

import com.rsk.http.client.HttpClientTask
import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.IHttpProxyTaskFactory
import com.rsk.http.proxy.Listeners
import com.rsk.http.proxy.ProxyBase
import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import com.rsk.logger
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream

class ProxyHttpServerTask(val connectionData: ConnectionData, val proxyTaskFactory: IHttpProxyTaskFactory, listeners: Listeners) :  HttpServerTask, ProxyBase() {

    var clientData: ProxyHttpServerData = ProxyHttpServerData()
    val headerListeners: MultiplexWriter
    val typeListeners: MultiplexOutputStream

    val Logger by logger()

    init {
        headerListeners = listeners.responseHeaderListeners
        typeListeners = listeners.responseTypeListeners
    }

    var running: Boolean = true

    override fun run() {
        Logger.debug("Starting the proxy server")
        val inputReader: BufferedReader = BufferedReader(InputStreamReader(connectionData.socket.inputStream))

        try {
            do {
                Logger.debug("Start consuming the request")
                consumeRequest(inputReader)
            } while (running)
        } finally {
            Logger.debug("Proxy server: Finish")
            connectionData.socket.close()
        }

    }

    private fun consumeRequest(reader: BufferedReader): Boolean {
        Logger.debug("Proxy server: consume request")
        val client = readHttpRequest( reader)
        readHttpHeaders(client, reader)
        return true
    }

    // todo: remove the null type and initialiser when passing the outputstream(writer?)
    internal fun readHttpRequest(bis: BufferedReader, os: OutputStream? = null): HttpClientTask {
        Logger.debug("Proxy server: reading request line")
        clientData.strRequestLine = bis.readLine() ?: ""
        Logger.debug("Proxy server: request line is ${clientData.strRequestLine}")
        connectionData.requestLine = clientData.strRequestLine

        Logger.debug("Execute the client task")
        //todo: Set listeners
        val client = proxyTaskFactory.createClientTask(connectionData)

        // wait for an event and forward the line
        Logger.debug("Proxy server: request line is ${clientData.strRequestLine}")
        return client
    }

    // todo: remove the null type and initialiser when passing the outputstream(writer?)
    internal fun readHttpHeaders(client: HttpClientTask, bis: BufferedReader, osServer: OutputStream? = null): Boolean {
        Logger.debug("Proxy server: reading headers")
        var header: String? = bis.readLine() ?: return false

        while (header != null && header.length != 0) {
            clientData.addHeader(header)
            Logger.debug("Proxy server: $header")
            header = bis.readLine()
        }
        return true
    }

}