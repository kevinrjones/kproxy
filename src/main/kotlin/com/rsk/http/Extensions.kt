/**
 * Created by kevinj on 03/09/2016.
 */
package com.rsk

import org.slf4j.Logger
import org.slf4j.LoggerFactory
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