package com.example.agoratesting

import android.util.Log
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class DefaultThreadFactory : ThreadFactory {
    private val threadNumber = AtomicInteger(1)
    private val group: ThreadGroup
    private val namePrefix: String

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix = "ARouter task pool No." + poolNumber.getAndIncrement() + ", thread No."
    }

    override fun newThread(runnable: Runnable): Thread {
        val threadName = namePrefix + threadNumber.getAndIncrement()
        Log.i(
            TAG,
            "Thread production, name is [$threadName]"
        )
        val thread = Thread(group, runnable, threadName, 0)
        if (thread.isDaemon) {   //Make non-background thread
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }

        // Catching exceptions in multi-threaded processing
        thread.uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { thread1: Thread, ex: Throwable ->
                Log.i(
                    TAG,
                    "Running task appeared exception! Thread [" + thread1.name + "], because [" + ex.message + "]"
                )
            }
        return thread
    }

    companion object {
        private val TAG = DefaultThreadFactory::class.java.simpleName
        private val poolNumber = AtomicInteger(1)
    }
}