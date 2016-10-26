package com.rsk.http.proxy

import com.rsk.http.CRLF
import com.rsk.logger
import com.rsk.readLine
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

abstract class ProxyBase {
    /**
     * Content length header name
     */
    internal val CONTENT_LENGTH = "CONTENT-LENGTH"
    /**
     * Host header name
     */
    internal val HOST = "HOST"
    /**
     * Transfer type header name
     */
    internal val TRANSFER_TYPE = "TRANSFER-ENCODING"
    /**
     * Content type header name
     */
    internal val CONTENT_TYPE = "CONTENT-TYPE"
    /**
     * Flag passed to [notifyAllListeners][.notifyAllListeners] function specifying
     * if the data is about to be presented to the listeners
     */
    internal val DATA_START = 1
    /**
     * Flag passed to [notifyAllListeners][.notifyAllListeners] function specifying
     * if the data output has ended
     */
    internal val DATA_END = 2

    val Logger by logger()

    @Throws(IOException::class)
    internal fun writeLine(osServer: OutputStream, line: String) {
        osServer.write("$line\r\n".toByteArray())
        osServer.flush()
    }

    @Throws(IOException::class)
    internal fun writeHeader(header: String, osServer: OutputStream) {
        writeLine(osServer, header)

        // todo: write the headers to the listeners?
    }

    @Throws(IOException::class)
    internal fun readContentLengthData(inputStream: InputStream, contentLength: Int): ByteArray {

        val bis = BufferedInputStream(inputStream)

        Logger.debug("readContentLengthData - contentLength to read is: $contentLength")
        val buf = ByteArray(contentLength)
        var size = 0;//bis.read(buf, 0, contentLength)
        while(size < contentLength) {
            size += bis.read(buf, size, contentLength - size)
        }
        Logger.debug("readContentLengthData - size read is: $size")
        Logger.debug("readContentLengthData - buffer size read is: ${buf.size}")

        return buf
    }

    fun readAndProcessHttpHeaders(writeHeader: (String) -> Unit, stream: InputStream, addHeader: (String) -> Unit): Boolean {
        Logger.debug("Proxy server: reading headers")
        var header: String? = stream.readLine()

        while (header != null && header.length != 0) {
            writeHeader(header)
            addHeader(header)
            Logger.debug("Proxy server: $header")
            header = stream.readLine()
        }
        writeHeader("")
        return true
    }

    @Throws(IOException::class)
    internal fun readData(inputStream: InputStream, writeData: (ByteArray) -> Unit, headers: Map<String, String>): ByteArray {
        val cl: String?
        val enc: String?
        val bis = BufferedInputStream(inputStream)
        // notify all listeners that we are about to send them data
        try {
            // do we have a content length header
            cl = headers.get(CONTENT_LENGTH)
            enc = headers.get(TRANSFER_TYPE)
            if (cl != null) {
                // get the value
                val contentLength = Integer.parseInt(cl)                // and read the data
                Logger.debug("ContentLength: " + contentLength)
                val data = readContentLengthData(bis, contentLength)
                writeData(data)
                return data
            } else if (enc != null) {
                // do we have 'chunked' encoding
                if ("chunked" == enc) {
                    return readChunkSizeData(bis, writeData)
                }
            } else {
                // if we get to here I have no Content-Length and no encoding
                // so we read to the end of the stream
                return bis.readBytes()
            }
        } finally {
            print("finished")
            // tell all listeners that we have no more data
            //notifyAllListeners(DATA_END)
        }
        return ByteArray(0)
    }

    @Throws(IOException::class)
    private fun readChunkSizeData(inputStream: InputStream, writeData: (ByteArray) -> Unit): ByteArray {
        var buffer: ByteArray = ByteArray(0)
        val bis = BufferedInputStream(inputStream)
        var currentChunkSize = readAndProcessChunkSize(bis, writeData)


        // get all chunks data
        while (currentChunkSize != 0) {
            val buf = ByteArray(currentChunkSize)
            // read as much as I can
            var n = bis.read(buf)

            Logger.debug("Initial Chunk Read: " + n)
            buffer += buf
            writeData(buf)

            // more to read for this chunk?
            while (n < currentChunkSize) {
                val size = bis.read(buf, 0, currentChunkSize - n)
                n += size
                Logger.debug("Chunk portion read: " + n)
                buffer += buf
                writeData(buf)
            }

            // chunked data is followed by a CRLF - read that
            bis.readLine()

            // and write it to the stream
            buffer += CRLF
            writeData(CRLF)

            // read next chunksize
            currentChunkSize = readAndProcessChunkSize(bis, writeData)
        }

        // last chunk has a CRLF read this off the i/stream and write it
        bis.readLine()
        buffer += CRLF
        writeData(CRLF)
        return buffer
    }

    private fun readAndProcessChunkSize(bis: BufferedInputStream, writeData: (ByteArray) -> Unit):  Int {
        // read the line containing the chunk size
        val chunkSizeLine = bis.readLine()
        val chunkSize = Integer.parseInt(chunkSizeLine, 16)

        // and forward it to all interested parties
        writeData(chunkSizeLine.toByteArray())
        writeData(CRLF)

        Logger.debug("ChunkSize: " + chunkSize)
        return chunkSize
    }

    fun writeEntity(data: ByteArray, outputStream: OutputStream) {
        outputStream.write(data)
        outputStream.flush()
    }
}