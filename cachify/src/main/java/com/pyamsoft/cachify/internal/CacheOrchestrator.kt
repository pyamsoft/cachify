/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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

import com.pyamsoft.cachify.Cache
import com.pyamsoft.cachify.storage.CacheStorage
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@PublishedApi
@ConsistentCopyVisibility
internal data class CacheOrchestrator<T : Any>
internal constructor(
    private val context: CoroutineContext,
    private val debugTag: String,
    private val storage: List<CacheStorage<T>>,
) : Cache {

  private val mutex = Mutex()
  private val logger = Logger(debugTag)
  private val runner = CacheRunner<T>(context, logger)

  override suspend fun clear() =
      withContext(context = context + NonCancellable) {
        logger.log { "Clear all caches" }
        // Coroutine scope here to make sure if anything throws an error we catch it in the scope
        coroutineScope { mutex.withLock { storage.forEach { it.clear() } } }
      }

  /**
   * Implements the cache-then-network policy
   *
   * Fetches data from caches that are valid otherwise it hits upstream
   */
  suspend fun fetch(upstream: suspend CoroutineScope.() -> T): T =
      withContext(context = context) {
        // Coroutine scope here to make sure if anything throws an error we catch it in the scope
        return@withContext coroutineScope {
          mutex.withLock {
            for (cache in storage) {
              val cached = cache.retrieve()
              if (cached != null) {
                logger.log { "Cached data from cache cache" }
                return@coroutineScope cached
              }
            }
          }

          val result = runner.fetch(this) { upstream() }
          logger.log { "Retrieved result from upstream: $result" }
          mutex.withLock { storage.forEach { it.cache(result) } }
          return@coroutineScope result
        }
      }
}
