/*
 * Copyright 2019 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.cachify

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicReference

/**
 * Adapted from https://gist.github.com/objcode/7ab4e7b1df8acd88696cb0ccecad16f7#file-concurrencyhelpers-kt-L124
 */
internal class CoroutineRunner<T : Any> internal constructor(debug: Boolean) {

    private val logger = Logger(debug)
    private val activeTask = AtomicReference<Deferred<T>?>(null)

    suspend inline fun joinOrRun(crossinline block: suspend () -> T): T {
        // Return if already running
        activeTask.get()
            ?.let { task ->
                when {
                    task.isCancelled -> logger.log { "Active task is found but it is already cancelled, ignore" }
                    task.isCompleted -> logger.log { "Active task is found but it is already completed, ignore" }
                    else -> {
                        logger.log { "Join already running task and await result" }
                        return task.await()
                    }
                }
            }

        return coroutineScope {
            // Create a new coroutine, but don't start it until it's decided that this block should
            // execute. In the code below, calling await() on newTask will cause this coroutine to
            // start.
            val newTask = async(start = CoroutineStart.LAZY) { block() }
            newTask.invokeOnCompletion {
                logger.log { "Runner task completed" }
                if (activeTask.compareAndSet(newTask, null)) {
                    logger.log { "Completed runner task cleared" }
                }
            }

            val result: T

            // Loop until we figure out if we need to run newTask, or if there is a task that's
            // already running we can join.
            while (true) {
                if (!activeTask.compareAndSet(null, newTask)) {
                    val currentTask = activeTask.get()
                    if (currentTask != null) {
                        logger.log { "Found existing task in loop..." }
                        val clearCurrent = when {
                            currentTask.isCancelled -> {
                                logger.log { "Existing task is already cancelled, clear out and yield" }
                                true
                            }
                            currentTask.isCompleted -> {
                                logger.log { "Existing task is already completed, clear out and yield" }
                                true
                            }
                            else -> false
                        }

                        if (clearCurrent) {
                            activeTask.set(null)
                            yield()
                        } else {
                            logger.log { "Existing task is live, attach and await result" }
                            newTask.cancel()
                            result = currentTask.await()
                            break
                        }
                    } else {
                        logger.log { "yield to find active task in loop" }
                        yield()
                    }
                } else {
                    logger.log { "Run new task and await result" }
                    result = newTask.await()
                    break
                }
            }

            logger.log { "Returning result from task" }
            return@coroutineScope result
        }
    }
}
