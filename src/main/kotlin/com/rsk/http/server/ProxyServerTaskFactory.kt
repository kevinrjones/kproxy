package com.rsk.http.server

import com.rsk.http.proxy.ConnectionData

class ProxyServerTaskFactory : HttpServerTaskFactory {
    override fun createServerTask(connectionData: ConnectionData): HttpServerTask {
        return ProxyHttpServerTask(connectionData)
    }

}