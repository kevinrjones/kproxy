package com.rsk.http.proxy

import com.rsk.http.client.HttpClientTask
import com.rsk.http.server.HttpServerTask

interface IHttpProxyTaskFactory {
    fun createServerTask(connectionData: ConnectionData): HttpServerTask

    fun createClientTask(connectionData: ConnectionData): HttpClientTask
}