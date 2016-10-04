package com.rsk.http.client

import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.Listeners
import com.rsk.http.proxy.ProxyBase
import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import com.rsk.logger
import java.net.URL

/**
 * request line is the HTTP request
 * as this is a proxy it should be the full line
 * GET http://foo.com/bar/quux HTTP/1.1
 */
class ProxyHttpClientTask(connectionData: ConnectionData, listeners: Listeners, val httpClient: IHttpClient) : HttpClientTask, ProxyBase() {

    val Logger by logger()

    val headerListeners: MultiplexWriter
    val typeListeners: MultiplexOutputStream

    val streamToOriginalClient = connectionData.socket.outputStream

    lateinit override var verb: String
    lateinit override var serverUrl: URL
    lateinit override var version: String
    override var port: Int? = null


    init {
        Logger.debug("Initialise ProxyHttpClientTask")
        headerListeners = listeners.responseHeaderListeners
        typeListeners = listeners.responseTypeListeners

        Logger.debug("Request URL is ${connectionData.requestLine}")
        val requestParts = connectionData.requestLine.split(' ')

        Logger.debug("Number of request parts is ${requestParts.size}")
        if ((requestParts.size != 3 || !requestParts[1].contains(':'))) {
            val message = "Request (${connectionData.requestLine}) is not in the correct format"
            Logger.error(message)
            throw IllegalArgumentException(message)
        }

        processRequestLine(requestParts)

        port = if (serverUrl.port == -1) 80 else serverUrl.port
        Logger.debug("ProxyHttpClientTask - verb: $verb, serverUrl: $serverUrl, version: $version, port: $port")

        processRequest()
    }

    private fun processRequestLine(requestParts: List<String>) {
        Logger.debug("Verb is '${requestParts[0]}'")
        Logger.debug("URL string is '${requestParts[1]}'")
        Logger.debug("Version is '${requestParts[2]}'")

        verb = requestParts[0]
        version = requestParts[2]

        val urlString = requestParts[1]

        if (urlString.startsWith("http")) {
            serverUrl = URL(requestParts[1])
        } else {
            if (urlString.endsWith("443")) {
                serverUrl = URL("https://$urlString")
            } else {
                serverUrl = URL("http://$urlString")
            }
        }

        Logger.debug("URL is '$serverUrl'")

    }

    override fun processRequest() {

        Logger.debug("Start connection: $serverUrl")

        Logger.debug("Create httpClient")

        val response = httpClient.executeCommand(serverUrl, verb)


        response.use {
            Logger.debug("HttpResponse status: ${response.statusLine}")

            //todo: stream data back to origin

            response.headers.forEach { headerListeners.write(it.name); headerListeners.write(":"); headerListeners.write(it.value); }
            response.entity.writeTo(typeListeners)
        }
        Logger.debug("Finished connection: $serverUrl")
    }

}

