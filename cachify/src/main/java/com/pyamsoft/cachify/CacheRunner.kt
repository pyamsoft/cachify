/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.cachify

import androidx.annotation.CheckResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@PublishedApi
internal class CacheRunner<T : Any> @PublishedApi internal constructor(
    /**
     * Logger
     *
     * @private
     */
    @PublishedApi
    internal val logger: Logger
) {

    /**
     * Mutex
     *
     * @private
     */
    @PublishedApi
    internal val mutex: Mutex = Mutex()

    /**
     * Runner
     *
     * @private
     */
    @PublishedApi
    internal var activeRunner: Runner<T>? = null

    /**
     * We must claim the mutex before checking task status because another task running in parallel
     * could be changing the activeTask value
     *
     * @private
     */
    @CheckResult
    @PublishedApi
    internal suspend fun joinExistingTask(): T? = mutex.withLock {
        logger.log { "Checking for active task" }
        return@withLock activeRunner?.let { active ->
            val id = active.id
            val task = active.task
            logger.log { "Active task join and await result: $id" }
            task.await()
        }
    }

    /**
     * Claim the lock and look for who's the active runner
     *
     * @private
     */
    @CheckResult
    @PublishedApi
    internal suspend inline fun createNewTask(
        scope: CoroutineScope,
        crossinline block: suspend CoroutineScope.() -> T
    ): Runner<T> = mutex.withLock {
        val active = activeRunner
        return@withLock if (active == null) {
            val currentId = randomId()
            val newTask = scope.async(start = CoroutineStart.LAZY) { block() }
            val newRunner = Runner(currentId, newTask)
            activeRunner = newRunner
            logger.log { "Marking task as active: $currentId" }
            newRunner
        } else {
            logger.log { "Found existing task, join: ${active.id}" }
            active
        }
    }

    /**
     * Await the completion of the task
     *
     * @private
     */
    @CheckResult
    @PublishedApi
    internal suspend fun runTask(runner: Runner<T>): T {
        logger.log { "Awaiting task ${runner.id}" }
        val result = runner.task.await()
        logger.log { "Completed task ${runner.id}" }
        return result
    }

    /**
     * Make sure the activeTask is actually us, otherwise we don't need to do anything
     * Fast path in this case only since we have the id to guard with as well as the state
     * of activeTask
     *
     * @private
     */
    @PublishedApi
    internal suspend fun clearActiveTask(runner: Runner<T>) {
        if (activeRunner?.id == runner.id) {
            // Run in the NonCancellable context because the mutex must be claimed to free the activeTask
            // or else we will leak memory.
            withContext(context = NonCancellable) {
                mutex.withLock {
                    // Check again to make sure we really are the active task
                    if (activeRunner?.id == runner.id) {
                        logger.log { "Releasing task ${runner.id} since it is complete" }
                        activeRunner = null
                    }
                }
            }
        }
    }

    suspend inline fun fetch(
        scope: CoroutineScope,
        crossinline block: suspend CoroutineScope.() -> T
    ): T {
        joinExistingTask()?.also { return it }
        val runner = createNewTask(scope, block)
        return try {
            runTask(runner)
        } finally {
            clearActiveTask(runner)
        }
    }

    /**
     * Runner holds a deferred identified by a unique id
     *
     * @private
     */
    @PublishedApi
    internal data class Runner<T : Any> @PublishedApi internal constructor(
        val id: String,
        val task: Deferred<T>
    )
}
