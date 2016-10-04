package com.rsk.http.client

import com.rsk.io.MultiplexWriter
import java.net.URI
import java.net.URL

interface HttpClientTask  {
    var verb: String
    var serverUrl: URL
    var version: String
    var port: Int?

    fun processRequest()
}