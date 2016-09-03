package com.rsk.http.server.integration

import com.rsk.http.server.HttpListener
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * Created by kevinj on 03/09/2016.
 */
class HttpListenerSpek : Spek ({

    val listener = HttpListener("GET http://localhost:8080 HTTP/1.1")

    describe("the http listener") {
        it("should connect to the server") {
            listener.start()
        }
    }
})