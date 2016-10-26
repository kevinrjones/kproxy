package com.rsk.http.server

import com.rsk.http.client.HttpClientTask
import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.IHttpProxyTaskFactory
import com.rsk.http.proxy.Listeners
import com.rsk.http.proxy.ProxyBase
import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import com.rsk.readLine
import java.io.*

class ProxyHttpServerTask(val connectionData: ConnectionData, val proxyTaskFactory: IHttpProxyTaskFactory, listeners: Listeners) : HttpServerTask, ProxyBase() {

    val clientData: ProxyHttpRequestData = ProxyHttpRequestData()
    val headerListeners: MultiplexWriter
    val typeListeners: MultiplexOutputStream

    init {
        headerListeners = listeners.responseHeaderListeners
        typeListeners = listeners.responseTypeListeners
    }

    var running: Boolean = true

    override fun run() {
        Logger.debug("Starting the proxy server")

        try {
            do {
                Logger.debug("Start consuming the request")
                consumeRequest(connectionData.socket.inputStream)
            } while (running)
        } catch (e: Exception) {
            Logger.error("consumeRequest has thrown an exception", e)
        }
        finally {
            Logger.debug("Proxy server: Finish")
            connectionData.socket.close()
        }
    }

    private fun consumeRequest(inputStream: InputStream): Boolean {
        Logger.debug("Proxy server: consume request")
        val client = readHttpRequest(inputStream)

        readAndProcessHttpHeaders({client.writeHeader(it)}, inputStream, { clientData.addHeader(it) })
        readAndProcessHttpEntity(client, inputStream)
        client.processResponse()
        return true
    }

    internal fun readHttpRequest(inputStream: InputStream): HttpClientTask {
        Logger.debug("Proxy server: reading request line")
        clientData.strRequestLine = inputStream.readLine()
        Logger.debug("Proxy server: request line is ${clientData.strRequestLine}")
        connectionData.requestLine = clientData.strRequestLine

        Logger.debug("Execute the client task")
        //todo: Set listeners
        val client = proxyTaskFactory.createClientTask(connectionData, this)

        // wait for an event and forward the line
        Logger.debug("Proxy server: request line is ${clientData.strRequestLine}")
        return client
    }

    private fun readAndProcessHttpEntity(client: HttpClientTask, inputStream: InputStream) {
        val cl: String?
        val data: ByteArray

        cl = clientData.headers[CONTENT_LENGTH]

        data = if (cl != null) {
            Logger.debug("Read data from client - size is $cl")
            readContentLengthData(inputStream, Integer.parseInt(cl))
        } else {
            ByteArray(0)
        }

        if (data.size > 0) client.writeEntity(data)
    }

    override fun writeHeader(header: String) {
        writeHeader(header, connectionData.socket.outputStream)
    }

    override fun writeEntity(data: ByteArray) {
        writeEntity(data, connectionData.socket.outputStream)
    }

}