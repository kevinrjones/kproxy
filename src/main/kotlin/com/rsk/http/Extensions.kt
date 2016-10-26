
/**
 * Created by kevinj on 03/09/2016.
 * see: http://stackoverflow.com/a/34462577/120599
 */
package com.rsk

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.companionObject

fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass).name) }
}

// unwrap companion class to enclosing class given a Java Class
fun <T: Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}

// unwrap companion class to enclosing class given a Kotlin Class
fun <T: Any> unwrapCompanionClass(ofClass: KClass<T>): KClass<*> {
    return unwrapCompanionClass(ofClass.java).kotlin
}

interface Loggable {}

fun Loggable.logger(): Logger {
    return LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass).name)
}

fun InputStream.readLine(): String {
    val readLineBuffer = ByteArray(1024)

    var i = -1
    var ret:Int
    do {
        i++
        ret = this.read(readLineBuffer, i, 1)
    } while (readLineBuffer[i].toChar() != '\n' && ret != -1)

    if (i == 0)
        return ""

    val str = String(readLineBuffer, 0, i - 1)
    return str
}