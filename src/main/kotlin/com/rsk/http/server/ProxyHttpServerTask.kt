package com.rsk.http.server

import com.rsk.http.proxy.ConnectionData
import com.rsk.http.proxy.ProxyBase
import com.rsk.logger
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream

// todo: separate the data into a separate class containing the headers etc;
// create an instance of that class for each request
class ProxyHttpServerTask(val connectionData: ConnectionData) : HttpServerTask, ProxyBase() {

    val Logger by logger()

    val headers: MutableMap<String, String> = mutableMapOf()


    override fun run() {
        Logger.debug("started server task")

        // start listening
        val inputReader: BufferedReader = BufferedReader(InputStreamReader(connectionData.serverSocket.inputStream))

        consumeRequest(inputReader)


    }

    private fun consumeRequest(reader: BufferedReader): Boolean {

        readHttpRequest(reader)
        readHttpHeaders(reader)
        return true

    }

    // todo: remove the null type and initialiser when passing the outputstream(writer?)
    internal fun readHttpRequest(bis: BufferedReader, os: OutputStream? = null): Boolean {
        val strRequestLine = bis.readLine()

        return false
    }

    // todo: remove the null type and initialiser when passing the outputstream(writer?)
    internal fun readHttpHeaders(bis: BufferedReader, osServer: OutputStream? = null): Boolean {
        var header: String? = bis.readLine() ?: return false

        while (header != null && header.length != 0) {
            addHeader(header)
            header = bis.readLine()
        }
        return true
    }

    internal fun addHeader(header: String) {
        var headerName: String = ""

        Logger.debug("addHeader:" + header)

        // header lines can continue, if a line starts with a space then it's a continuation
        if (header[0] == ' ' || header[0] == '\t') {
            Logger.debug("space in header")
            var headerValue: String = headers.getOrElse(headerName.toUpperCase()) { "" }
            headerValue += header
            headers.put(headerName.toUpperCase(), headerValue)
        } else {
            headerName = header.substring(0, header.indexOf(':'))
            val value = header.substring(header.indexOf(':') + 1)

            headers.put(headerName.toUpperCase(), value.trim { it <= ' ' })
        }
    }
}