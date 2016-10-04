package com.rsk.http.socket

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress

interface ISocket {
    val inetAddress: InetAddress
    val inputStream: InputStream
    val outputStream: OutputStream
    val port: Int

    fun close()

}