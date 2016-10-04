package com.rsk.http.client

import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.client.methods.*
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class ApacheHttpClient : IHttpClient {
    override fun executeCommand(url: URL, verb: String): ProxyHttpResponse {

        val httpclient = HttpClients.createDefault()
        val request: HttpRequestBase
        when (verb) {
            "GET" -> request = HttpGet(url.toURI())
            "POST" -> request = HttpPost(url.toURI())
            "PUT" -> request = HttpPut(url.toURI())
            "OPTIONS" -> request = HttpOptions(url.toURI())
            "HEAD" -> request = HttpHead(url.toURI())
            "DELETE" -> request = HttpDelete(url.toURI())
            "TRACE" -> request = HttpTrace(url.toURI())
            else -> request = HttpGet(url.toURI())
        }

        return ApacheProxyHttpResponse(httpclient.execute(request))
    }

}

class ApacheProxyHttpResponse(val closeableHttpResponse: CloseableHttpResponse) : ProxyHttpResponse {
    override val entity: ProxyHttpEntity
        get() = ApacheProxyHttpEntity(closeableHttpResponse.entity)

    override val headers: List<ProxyHttpHeader>
        get() {
            var headers = listOf<ProxyHttpHeader>()

            closeableHttpResponse.allHeaders.forEach {
                headers = headers.plus(ProxyHttpHeader(it.name, it.value))
            }

            return  headers
        }

    override val statusLine: String
        get() = "${closeableHttpResponse.statusLine.statusCode} " +
                "${closeableHttpResponse.statusLine.reasonPhrase} " +
                "${closeableHttpResponse.statusLine.protocolVersion}"


    override fun close() {
        closeableHttpResponse.close()
    }

}

class ApacheProxyHttpEntity (val httpEntity: HttpEntity): ProxyHttpEntity {
    override val chunked: Boolean
        get() = httpEntity.isChunked
    override val ContentLength: Long
        get() = httpEntity.contentLength
    override val ContentType: ProxyHttpHeader
        get() = ProxyHttpHeader(httpEntity.contentType.name, httpEntity.contentType.value)
    override val ContentEncoding: ProxyHttpHeader
        get() = ProxyHttpHeader(httpEntity.contentEncoding.name, httpEntity.contentEncoding.value)

    override fun getContent(): InputStream {
        return httpEntity.content
    }

    override fun writeTo(outstream: OutputStream) {
        httpEntity.writeTo(outstream)
    }

    override fun isStreaming(): Boolean {
        return httpEntity.isStreaming
    }


}