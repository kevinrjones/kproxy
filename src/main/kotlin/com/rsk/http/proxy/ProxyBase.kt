package com.rsk.http.proxy

abstract class ProxyBase  {
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



}