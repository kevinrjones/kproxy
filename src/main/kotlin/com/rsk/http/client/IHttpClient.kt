package com.rsk.http.client

import com.rsk.http.socket.ISocket
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

interface IHttpClient {
    fun createConnection(url:  String, port: Int?): ISocket
//    fun executeCommand(url: URL, verb: String): ProxyHttpResponse
}

interface ProxyHttpResponse : Closeable {
    val statusLine: String
    val headers: List<ProxyHttpHeader>
    val entity: ProxyHttpEntity
}

data class ProxyHttpHeader(val name: String, val value: String)

interface ProxyHttpEntity {

    val chunked: Boolean

    val ContentLength: Long

    val ContentType: ProxyHttpHeader

    val ContentEncoding: ProxyHttpHeader

    
    @Throws(IOException::class, UnsupportedOperationException::class)
    fun getContent(): InputStream

    @Throws(IOException::class)
    fun writeTo(outstream: OutputStream)

    fun isStreaming(): Boolean  
}