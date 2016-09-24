package com.rsk.threading

import com.rsk.logger
import java.util.concurrent.*

class ExceptionHandlingThreadPool(numberOfThreads: Int) : ThreadPoolExecutor(numberOfThreads, numberOfThreads,
        0, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>()) {

    val Logger by logger()

    override fun afterExecute(runnable: Runnable, exception: Throwable?) {
        super.afterExecute(runnable, exception)
        var throwable = exception
        if (throwable == null && runnable is Future<*>) {
            try {
                if (runnable.isDone) {
                    runnable.get()
                }
            } catch (ce: CancellationException) {
                throwable = ce
            } catch (ee: ExecutionException) {
                throwable = ee.cause
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt() // ignore/reset
            }

        }
        if (throwable != null) {
            Logger.error("Error thrown by task", throwable)
        }
    }
}


// core threads
// max threads
// timeout units
// work queue