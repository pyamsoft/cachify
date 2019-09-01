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
 * Interface contract which Cachify uses to save data into a representation of cache
 */
interface CacheStorage<T> {

    /**
     * Get any data from cache if it exists - otherwise null
     */
    @CheckResult
    suspend fun retrieve(): T?

    /**
     * Sets new data as the active data in cache
     *
     * The new data is valid for as long as the implementation considers it valid. It may be infinite.
     */
    suspend fun set(data: T)

    /**
     * Clears all data from the cache - effectively resetting it
     */
    suspend fun clear()
}
