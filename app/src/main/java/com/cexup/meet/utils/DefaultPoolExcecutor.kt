package com.cexup.meet.utils

import android.util.Log
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DefaultPoolExecutor private constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    threadFactory: ThreadFactory
) :
    ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
        RejectedExecutionHandler { r, executor -> Log.e(TAG, "Task rejected, too many task!") }) {
    /* thread execution complete, handle possible exceptions.
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if
     */
    override fun afterExecute(r: Runnable?, t: Throwable?) {
        var t: Throwable? = t
        super.afterExecute(r, t)
        if (t == null && r is Future<*>) {
            try {
                (r as Future<*>).get()
            } catch (ce: CancellationException) {
                t = ce
            } catch (ee: ExecutionException) {
                t = ee.cause
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt() // ignore/reset
            }
        }
        if (t != null) {
            Log.w(
                TAG,
                """
                    Running task appeared exception! Thread [${Thread.currentThread().name}], because [${t.message}]
                    ${TextUtils.formatStackTrace(t.stackTrace)}
                    """.trimIndent()
            )
        }
    }

    companion object {
        private val TAG = DefaultPoolExecutor::class.java.simpleName

        //    Thread args
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val INIT_THREAD_COUNT = CPU_COUNT + 1
        private val MAX_THREAD_COUNT = INIT_THREAD_COUNT
        private const val SURPLUS_THREAD_LIFE = 30L
        var instance: DefaultPoolExecutor? = null
            get() {
                if (null == field) {
                    synchronized(DefaultPoolExecutor::class.java) {
                        if (null == field) {
                            field = DefaultPoolExecutor(
                                INIT_THREAD_COUNT,
                                MAX_THREAD_COUNT,
                                SURPLUS_THREAD_LIFE,
                                TimeUnit.SECONDS,
                                ArrayBlockingQueue<Runnable>(64),
                                DefaultThreadFactory()
                            )
                        }
                    }
                }
                return field
            }
            private set
    }
}
