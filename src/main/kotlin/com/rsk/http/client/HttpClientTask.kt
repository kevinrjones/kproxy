package com.rsk.http.client

import com.rsk.logger
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.net.URI

/**
 * request line is the HTTP request
 * as this is a proxy it should be the full line
 * GET http://foo.com/bar/quux HTTP/1.1
 */
class HttpClientTask(requestLine: String) {

    val Logger by logger()

    val verb: String
    val serverUrl: URI
    val version: String
    val port: Int


    init {
        val requestParts = requestLine.split(' ')
        verb = requestParts[0]
        serverUrl = URI(requestParts[1])
        version = requestParts[2]

        port = if (serverUrl.port == -1) 80 else serverUrl.port
    }

    fun start() {

        Logger.debug("Start connection: $serverUrl")

        val httpclient = HttpClients.createDefault()
        val httpGet = HttpGet(serverUrl)
        val response1 = httpclient.execute(httpGet)

        response1.use {
            System.out.println(response1.statusLine)
            val entity1 = response1.entity
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1)
        }
    }
}
