package com.rsk.http.client

import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.Listeners
import com.rsk.http.proxy.ProxyBase
import com.rsk.io.MultiplexWriter
import com.rsk.logger
import org.apache.http.client.methods.*
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.net.URL

/**
 * request line is the HTTP request
 * as this is a proxy it should be the full line
 * GET http://foo.com/bar/quux HTTP/1.1
 */
class ProxyHttpClientTask(connectionData: ConnectionData, listeners: Listeners) : HttpClientTask, ProxyBase() {


    val Logger by logger()

    val headerListeners: MultiplexWriter
    val typeListeners: MultiplexWriter

    override var verb: String? = null
    override var serverUrl: URL? = null
    override var version: String? = null
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


        port = if (serverUrl?.port == -1) 80 else serverUrl?.port
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
                serverUrl = URL("https://${urlString}")
            } else {
                serverUrl = URL("http://${urlString}")
            }
        }

        Logger.debug("URL is '$serverUrl'")

    }

    override fun processRequest() {

        Logger.debug("Start connection: $serverUrl")

        Logger.debug("Create httpClient")
        val httpclient = HttpClients.createDefault()
        val request: HttpRequestBase
        when (verb) {
            "GET" -> request = HttpGet(serverUrl?.toURI())
            "POST" -> request = HttpPost(serverUrl?.toURI())
            "PUT" -> request = HttpPut(serverUrl?.toURI())
            "OPTIONS" -> request = HttpOptions(serverUrl?.toURI())
            "HEAD" -> request = HttpHead(serverUrl?.toURI())
            "DELETE" -> request = HttpDelete(serverUrl?.toURI())
            "TRACE" -> request = HttpTrace(serverUrl?.toURI())
            else -> request = HttpGet(serverUrl?.toURI())
        }

        val response1 = httpclient.execute(request)

        response1.use {
            Logger.debug("HttpResponse status: ${response1.statusLine}")
            val entity1 = response1.entity
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1)
        }
        Logger.debug("Finished connection: $serverUrl")
    }

    override fun setListeners(headerListeners: MultiplexWriter, typeListeners: MultiplexWriter) {
//        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

