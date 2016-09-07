package com.rsk.http.server

import com.rsk.http.proxy.ConnectionData

interface HttpServerTaskFactory {
    fun createServerTask(connectionData: ConnectionData): HttpServerTask
}