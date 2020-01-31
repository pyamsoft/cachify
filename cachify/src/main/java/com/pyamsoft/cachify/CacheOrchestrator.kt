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

import kotlinx.coroutines.CoroutineScope

internal abstract class CacheOrchestrator<R : Any> protected constructor(
    debug: Boolean,
    storage: List<CacheStorage<R>>
) : Cache {

    private val logger = Logger(enabled = debug)
    private val runner = CacheRunner<R>(debug = debug)
    private val caches = storage.map { ActualCache(it, debug) }

    override fun clear() {
        logger.log { "Clear all caches" }
        caches.forEach { it.clear() }
    }

    suspend fun callCache(upstream: suspend CoroutineScope.() -> R): R {
        return runner.call {
            for ((index, cache) in caches.withIndex()) {
                val cached = cache.retrieve()
                if (cached != null) {
                    logger.log { "Cached data from cache #$index" }
                    return@call cached
                }
            }

            logger.log { "Fetching data from upstream" }
            val result = upstream()
            logger.log { "Retrieved result from upstream: $result" }
            caches.forEach { it.cache(result) }
            return@call result
        }
    }
}
