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

import androidx.annotation.CheckResult
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class CacheRunner<T : Any> internal constructor(private val logger: Logger) {

    private val counter = WrapAroundCounter(0)
    private val mutex = Mutex()
    private var activeTask: RunnerTask<T>? = null

    suspend fun run(block: suspend CoroutineScope.() -> T): T {
        // We must claim the mutex before checking task status because another task running in parallel
        // could be changing the activeTask value
        val (currentId: Long, newTask: Deferred<T>) = mutex.withLock {
            activeTask?.also { active ->
                val activeId = active.id
                val task = active.task
                when {
                    task.isCancelled -> logger.log { "Active task but already cancelled: $activeId" }
                    task.isCompleted -> logger.log { "Active task but already completed: $activeId" }
                    else -> {
                        // Return if already running
                        logger.log { "Active task join and await result: $activeId" }
                        return task.await()
                    }
                }
            }

            // Create a new coroutine, but don't start it until it's decided that this block should
            // execute. In the code below, calling await() on newTask will cause this coroutine to
            // start.
            return@withLock coroutineScope {
                val lazyTask = async(start = CoroutineStart.LAZY) { block() }

                // Make sure we mark this task as the active task
                // A new random id which signifies this running block
                val currentId = counter.get()
                logger.log { "Marking task as active: $currentId" }
                activeTask = RunnerTask(currentId, lazyTask)

                // Return this task
                return@coroutineScope currentId to lazyTask
            }
        }

        // Await the completion of the task
        try {
            val result = newTask.await()
            logger.log { "Returning result from task $currentId" }
            return result
        } finally {
            // Make sure the activeTask is actually us, otherwise we don't need to do anything
            // Fast path in this case only since we have the id to guard with as well as the state
            // of activeTask
            if (activeTask?.id == currentId) {
                // Run in the NonCancellable context because the mutex must be claimed to free the activeTask
                // or else we will leak memory.
                withContext(context = NonCancellable) {
                    mutex.withLock {
                        // Check again to make sure we really are the active task
                        if (activeTask?.id == currentId) {
                            logger.log { "Releasing task $currentId since it is complete" }
                            activeTask = null
                        }
                    }
                }
            }
        }
    }

    private data class RunnerTask<T : Any> internal constructor(
        val id: Long,
        val task: Deferred<T>
    )

    private data class WrapAroundCounter internal constructor(
        private var count: Long
    ) {

        @CheckResult
        fun get(): Long {
            if (count >= 1_000_000) {
                count = 0
            }
            return count++
        }

    }
}
