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

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class CacheOrchestrator<T : Any>
internal constructor(
    private val context: CoroutineContext,
    debugTag: String,
    private val storage: List<CacheStorage<T>>,
) : CacheOperator<T> {

  private val mutex = Mutex()
  private val logger: Logger = Logger(debugTag)
  private val runner: CacheRunner<T> = CacheRunner(logger)

  override suspend fun clear() =
      withContext(context = NonCancellable) {
        // Maybe we can simplify this with a withContext(context = NonCancellable + context)
        // but I don't know enough about Coroutines right now to figure out if that works
        // or if plussing the contexts will remove NonCancel, so here we go instead.
        logger.log { "Clear all caches" }
        withContext(context = context) {
          // Coroutine scope here to make sure if anything throws an error we catch it in the scope
          coroutineScope { mutex.withLock { storage.forEach { it.clear() } } }
        }
      }

  override suspend fun cache(upstream: suspend CoroutineScope.() -> T): T =
      withContext(context = context) {
        logger.log { "Running call for cache" }

        // Coroutine scope here to make sure if anything throws an error we catch it in the scope
        return@withContext coroutineScope {
          mutex.withLock {
            for (index in storage.indices) {
              val cache = storage[index]
              val cached = cache.retrieve()
              if (cached != null) {
                logger.log { "Cached data from cache #$index" }
                return@coroutineScope cached
              }
            }
          }

          val result =
              runner.fetch(this) {
                logger.log { "Fetching data from upstream" }
                return@fetch upstream()
              }

          logger.log { "Retrieved result from upstream: $result" }
          mutex.withLock { storage.forEach { it.cache(result) } }
          return@coroutineScope result
        }
      }
}
