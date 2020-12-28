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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

internal class CacheOrchestrator<K : Any, V : Any> internal constructor(
    debugTag: String,
    private val storage: List<CacheStorage<K, V>>
) : CacheOperator<K, V> {

    private val logger: Logger = Logger(debugTag)
    private val runner: CacheRunner<V> = CacheRunner(logger)

    override suspend fun clear() = coroutineScope {
        logger.log { "Clear all caches" }
        storage.forEach { it.clear() }
    }

    override suspend fun invalidate(key: K) = coroutineScope {
        logger.log { "Invalidate all caches with key: $key" }
        storage.forEach { it.invalidate(key) }
    }

    override suspend fun cache(key: K, upstream: suspend CoroutineScope.() -> V): V =
        coroutineScope {
            logger.log { "Running call for cache: $key" }
            for ((index, cache) in storage.withIndex()) {
                val cached = cache.retrieve(key)
                if (cached != null) {
                    logger.log { "Cached data from cache $key[#$index]" }
                    return@coroutineScope cached
                }
            }

            val result = runner.fetch(this) {
                logger.log { "Fetching data from upstream for $key" }
                return@fetch upstream()
            }

            logger.log { "Retrieved result from upstream: [$key]=$result" }
            storage.forEach { it.cache(key, result) }
            return@coroutineScope result
        }
}
