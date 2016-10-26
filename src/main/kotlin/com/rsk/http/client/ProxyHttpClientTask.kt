package com.rsk.http.client

import com.rsk.ProxyException
import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.Listeners
import com.rsk.http.proxy.ProxyBase
import com.rsk.http.server.HttpServerTask
import com.rsk.http.server.ProxyHttpResponseData
import com.rsk.http.socket.ISocket
import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter
import com.rsk.readLine
import java.io.InputStream
import java.net.URL

/**
 * request line is the HTTP request
 * as this is a proxy it should be the full line
 * GET http://foo.com/bar/quux HTTP/1.1
 */
class ProxyHttpClientTask(connectionData: ConnectionData, listeners: Listeners, val httpClient: IHttpClient, val server: HttpServerTask) : HttpClientTask, ProxyBase() {

    /**
     * Where the response code starts in the HTTP status line
     */
    private val HTTP_RESPONSE_CODE_OFFSET_START = 9

    /**
     * Where the response code ends in the HTTP status line
     */
    private val HTTP_RESPONSE_CODE_OFFSET_END = 12

    val serverData: ProxyHttpResponseData = ProxyHttpResponseData()
    val headerListeners: MultiplexWriter
    val typeListeners: MultiplexOutputStream

    lateinit override var serverSocket: ISocket

    lateinit override var verb: String
    lateinit override var serverUrl: URL
    lateinit override var version: String
    lateinit override var entityUrl: String
    override var port: Int? = null
    var requestLine: String


    init {
        Logger.debug("Initialise ProxyHttpClientTask")
        headerListeners = listeners.responseHeaderListeners
        typeListeners = listeners.responseTypeListeners

        Logger.debug("Request URL is ${connectionData.requestLine}")
        requestLine = connectionData.requestLine
        val requestParts = connectionData.requestLine.split(' ')

        Logger.debug("Number of request parts is ${requestParts.size}")
        if ((requestParts.size != 3 || !requestParts[1].contains(':'))) {
            val message = "Request (${connectionData.requestLine}) is not in the correct format"
            Logger.error(message)
            throw IllegalArgumentException(message)
        }

        parseRequestLine(requestParts)

        port = if (serverUrl.port == -1) 80 else serverUrl.port
        Logger.debug("ProxyHttpClientTask - verb: $verb, serverUrl: $serverUrl, version: $version, port: $port")

        startConnection()
        processRequest(entityUrl)
    }

    fun processRequest(entrityUrl: String) {
        if (verb != "CONNECT") {
            writeRequestLine("$verb $entrityUrl $version")
        } else {
            // todo: works for now but really need to tunnel
            writeRequestLine("$verb $entrityUrl $version")
        }
    }

    // todo: test for this
    fun writeRequestLine(requestLine: String) {
        writeLine(serverSocket.outputStream, requestLine)
    }

    override fun writeHeader(header: String) {
        writeHeader(header, serverSocket.outputStream)
    }

    override fun writeEntity(data: ByteArray) {
        writeEntity(data, serverSocket.outputStream)
    }

    private fun parseRequestLine(requestParts: List<String>) {
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

        entityUrl = serverUrl.file

        Logger.debug("URL is '$serverUrl'")
        Logger.debug("entityUrl is '$entityUrl'")
    }

    override fun startConnection() {

        Logger.debug("Start connection: ${serverUrl.host}:$port")

        serverSocket = httpClient.createConnection(serverUrl.host, port)
    }


    override fun processResponse() {
        if (verb != "CONNECT") {
            readHttpResponse(serverSocket.inputStream)
            readHttpHeaders(serverSocket.inputStream)

            if (expectingData) {
                serverData.entity = readData(serverSocket.inputStream, { server.writeEntity(it) }, serverData.headers)
            }
        }
    }

    private fun readHttpHeaders(inputStream: InputStream) {
        readAndProcessHttpHeaders({ server.writeHeader(it) }, inputStream, { serverData.addHeader(it) })
    }

    private fun readHttpResponse(inputStream: InputStream) {
        serverData.strResponseLine = inputStream.readLine()
        if (serverData.strResponseLine.isNullOrEmpty()) throw ProxyException("Expected a response line here")
        Logger.debug("ProxyClient: readHttpResponse: " + serverData.strResponseLine)
        serverData.responseCode = Integer.parseInt(serverData.strResponseLine.substring(HTTP_RESPONSE_CODE_OFFSET_START, HTTP_RESPONSE_CODE_OFFSET_END))
        server.writeHeader(serverData.strResponseLine)
    }

    private val expectingData: Boolean by lazy {
        !(serverData.responseCode == 304 || serverData.responseCode == 204
                || serverData.responseCode >= 100 && serverData.responseCode < 200)
    }

}

