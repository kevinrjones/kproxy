package com.rsk.http.proxy

import com.rsk.io.MultiplexOutputStream
import com.rsk.io.MultiplexWriter

data class Listeners(val responseHeaderListeners: MultiplexWriter, val responseTypeListeners: MultiplexOutputStream)