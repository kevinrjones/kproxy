package com.rsk.http.server

import java.io.OutputStream

interface HttpTask {
//    fun writeEntity(data: ByteArray, outputStream: OutputStream)
    fun writeEntity(data: ByteArray)
    fun writeHeader(header: String)
}