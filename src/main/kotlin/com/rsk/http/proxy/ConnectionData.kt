package com.rsk.http.proxy

import com.rsk.http.socket.ISocket
import java.net.Socket
import java.util.*

/**
 * Created by kevin on 04/09/2016.
 */
data class ConnectionData (
        val serverSocket: ISocket,
        var state: String,
        val dateStarted: Date,
        val requestHost: String,
        var targetHost: String = "",
        var request: String = "",
        var connectionId: Int = -1,
        var listenPort: Int = 0
)
