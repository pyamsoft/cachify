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

/**
 * Performs cache operations
 */
public interface CacheOperator<K : Any, V : Any> : Cache<K> {

    /**
     * Invalidate a cache entry given key K
     */
    public suspend fun invalidate(key: K)

    /**
     * Cache new data from an upstream into key K
     */
    @CheckResult
    public suspend fun cache(key: K, upstream: suspend CoroutineScope.() -> V): V

    public companion object {

        @CheckResult
        @PublishedApi
        internal fun <K : Any, V : Any> create(
            debugTag: String,
            storage: List<CacheStorage<K, V>>
        ): CacheOperator<K, V> {
            return CacheOrchestrator(debugTag, storage)
        }
    }
}
