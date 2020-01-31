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

/**
 * Internal
 *
 * Handles the caching strategy for a given upstream data.
 *
 * The runner will re-attach to any in progress requests when the cache misses.
 */
internal class ActualCache<R : Any> internal constructor(
    private val storage: CacheStorage<R>,
    debug: Boolean
) : CacheStorage<R> {

    private val logger = Logger(enabled = debug)

    override fun clear() {
        logger.log { "Clear cached data" }
        storage.clear()
    }

    @CheckResult
    override fun retrieve(): R? {
        logger.log { "Retrieve cached data" }
        return storage.retrieve()
    }

    override fun cache(data: R) {
        logger.log { "Cache new data: $data" }
        storage.cache(data)
    }
}
