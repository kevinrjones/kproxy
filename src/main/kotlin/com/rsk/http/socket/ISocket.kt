package com.rsk.http.socket

import java.io.InputStream
import java.net.InetAddress

interface ISocket {
    val inetAddress: InetAddress
    val inputStream: InputStream

}