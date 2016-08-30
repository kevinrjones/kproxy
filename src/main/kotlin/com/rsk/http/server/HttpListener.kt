package com.rsk.http.server

/**
 * request line is the HTTP request
 * as this is a proxy it should be the full line
 * GET http://foo.com/bar/quux HTTP/1.1
 */
class HttpListener(requetLine: String) {

    val verb:String
    val server:String
    val version:String
    val port:Int

    init {
        verb  = "GET"
        server = ""
        version = ""
        port = 0
    }
}