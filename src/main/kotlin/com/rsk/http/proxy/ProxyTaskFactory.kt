package com.rsk.http.proxy

import com.rsk.http.client.HttpClientTask
import com.rsk.http.client.IHttpClient
import com.rsk.http.client.ProxyHttpClientTask
import com.rsk.http.server.HttpServerTask
import com.rsk.http.server.ProxyHttpServerTask

class ProxyTaskFactory(val serverListeners: Listeners, val clientListeners: Listeners, val httpClient: IHttpClient) : IHttpProxyTaskFactory {


    override fun createServerTask(connectionData: ConnectionData): HttpServerTask {
        return ProxyHttpServerTask(connectionData, this, serverListeners)
    }

    override fun createClientTask(connectionData: ConnectionData, server: HttpServerTask): HttpClientTask {
        return ProxyHttpClientTask(connectionData, clientListeners, httpClient, server)
    }

}

