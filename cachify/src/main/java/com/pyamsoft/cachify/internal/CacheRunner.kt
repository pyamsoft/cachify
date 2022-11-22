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

package com.pyamsoft.cachify.internal

import androidx.annotation.CheckResult
import java.util.UUID
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class CacheRunner<T : Any>
internal constructor(
    private val context: CoroutineContext,
    private val logger: Logger,
) {

  private val mutex: Mutex = Mutex()

  private var activeRunner: Runner<T>? = null

  /**
   * NEEDS TO BE LOCKED WITH mutex.withLock {}
   *
   * Await the completion of the task
   */
  @CheckResult
  private suspend fun runTask(runner: Runner<T>): T =
      withContext(context = context) {
        logger.log { "Running task ${runner.id}" }

        // Must call explicit start because this is a LAZY coroutine
        runner.task.start()

        logger.log { "Awaiting task ${runner.id}" }
        return@withContext runner.task.await().also { logger.log { "Completed task ${runner.id}" } }
      }

  /**
   * We must claim the mutex before checking task status because another task running in parallel
   * could be changing the activeTask value
   */
  @CheckResult
  private suspend fun joinExistingTask(): T? {
    // No active task, return null to continue
    val initialRunner = activeRunner ?: return null

    logger.log { "Attempt to join ActiveRunner outside lock: ${initialRunner.id}" }
    return mutex.withLock {
      val lockedRunner = activeRunner
      if (lockedRunner == null) {
        logger.log { "Inside lock, no ActiveRunner to join" }
        return@withLock null
      }

      logger.log { "Join existing runner ${lockedRunner.id}" }
      return@withLock runTask(lockedRunner)
    }
  }

  /** Claim the lock and look for who's the active runner */
  @CheckResult
  private suspend inline fun createNewTask(
      scope: CoroutineScope,
      crossinline block: suspend CoroutineScope.() -> T
  ): Runner<T> =
      mutex.withLock {
        val active = activeRunner
        return@withLock if (active == null) {
          // No activeRunner, create a new one
          Runner(
                  // Start the Deferred as Lazy so that it will not start
                  // until we explicitly start() it
                  task =
                      scope.async(
                          context = context,
                          start = CoroutineStart.LAZY,
                      ) {
                        block()
                      },
              )
              .also { runner ->
                activeRunner = runner
                logger.log { "Marking runner as active: ${runner.id}" }
              }
        } else {
          // Return the existing runner for joins
          logger.log { "Found existing runner, join: ${active.id}" }
          active
        }
      }

  /**
   * Make sure the activeTask is actually us, otherwise we don't need to do anything Fast path in
   * this case only since we have the id to guard with as well as the state of activeTask
   */
  private suspend fun clearActiveTask(runner: Runner<T>) =
      withContext(context = NonCancellable) {
        // Run in the NonCancellable context because the mutex must be claimed to free the
        // activeTask or else we will leak memory.
        val initialRunner = activeRunner
        if (initialRunner?.id == runner.id) {
          logger.log { "ActiveRunner outside lock is current, clearing! ${runner.id}" }
          mutex.withLock {
            // Check again to make sure we really are the active task
            // Since someone else could have become the active task in
            // between us checking above and us claiming the lock here
            val lockedRunner = activeRunner
            if (lockedRunner?.id == runner.id) {
              logger.log { "Releasing ActiveRunner ${runner.id} since it is complete" }

              // Since this is called as finally, we don't need to cancel the task here.
              // It is either cancelled already or it is completed.
              // Just null out the field for memory.
              activeRunner = null
            }
          }
        }
      }

  suspend inline fun fetch(
      scope: CoroutineScope,
      crossinline block: suspend CoroutineScope.() -> T
  ): T =
      withContext(context = context) {
        // Potentially locks mutex
        val existing = joinExistingTask()
        if (existing != null) {
          return@withContext existing
        }

        // Locks mutex
        val runner = createNewTask(scope, block)
        return@withContext try {
          logger.log { "Fetch new data from upstream" }
          runTask(runner)
        } finally {

          // Potentially locks mutex
          //
          // If something like this happens
          // call() --> creates task T1
          //    -- creates new task T1
          //    -- runs new task T1
          //    -- waits a long ass time for T1
          // call() --> adopts task T1 or creates task T2
          //    -- depending on T1 behavior:
          //    --   If T1 still running, join and return (does not make another upstream call)
          //    --   If T1 is completed, create new task T2 and run
          //    -- finally frees task that is active (T1 if adopted, T2 if created)
          clearActiveTask(runner)
        }
      }

  /** Runner holds a deferred identified by a unique id */
  private data class Runner<T : Any>(
      val task: Deferred<T>,
      val id: String = randomId(),
  )

  companion object {

    /** Generate a random UUID */
    @JvmStatic
    @CheckResult
    private fun randomId(): String {
      return UUID.randomUUID().toString()
    }
  }
}
