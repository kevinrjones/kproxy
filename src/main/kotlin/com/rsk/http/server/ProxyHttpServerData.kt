package com.rsk.http.server

import com.rsk.logger

class ProxyHttpServerData {

    val Logger by logger()

    internal fun addHeader(header: String) {
        var headerName: String = ""

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

    val headers: MutableMap<String, String> = mutableMapOf()
    var strRequestLine = ""

}