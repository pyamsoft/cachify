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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Internal
 *
 * Handles the caching strategy for a given upstream data.
 *
 * The runner will re-attach to any in progress requests when the cache misses.
 */
internal class ActualCache<R : Any> internal constructor(
    private val mutex: Mutex,
    private val storage: CacheStorage<R>,
    debug: Boolean
) : Cache {

    private val logger = Logger(enabled = debug)

    override suspend fun clear() {
        logger.log { "Clear cached data" }
        mutex.withLock { storage.clear() }
    }

    @CheckResult
    suspend fun retrieve(): R? {
        return mutex.withLock { storage.retrieve() }
    }

    suspend fun cache(data: R) {
        mutex.withLock { storage.set(data) }
    }
}
