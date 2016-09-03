package com.rsk.http.server

import com.rsk.logger
import java.net.URL

/**
 * request line is the HTTP request
 * as this is a proxy it should be the full line
 * GET http://foo.com/bar/quux HTTP/1.1
 */
class HttpListener(requestLine: String) {

    val Logger by logger()

    val verb: String
    val serverUrl: URL
    val version: String
    val port: Int


    init {
        val requestParts = requestLine.split(' ')
        verb = requestParts[0]
        serverUrl = URL(requestParts[1])
        version = requestParts[2]


        port = if (serverUrl.port == -1) 80 else serverUrl.port
    }

    fun start() {

        Logger.debug("Start connection: $serverUrl")
    }
}
