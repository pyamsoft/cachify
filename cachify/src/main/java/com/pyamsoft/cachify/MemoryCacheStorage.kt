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
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * CacheStorage implementation which is backed by memory. Short lived cache.
 */
class MemoryCacheStorage<T : Any> internal constructor(
    private val ttl: Long,
    debug: Boolean
) : CacheStorage<T> {

    private val lock = Any()
    private val logger = Logger(enabled = debug)
    private val storage = AtomicReference<T>(null)
    private val lastAccessTime = AtomicLong(0)

    override fun retrieve(): T? {
        return synchronized(lock) {
            val cached: T? = storage.get()
            return@synchronized when {
                cached == null -> {
                    logger.log { "No cached data, retrieve null" }
                    null
                }
                lastAccessTime.get() + ttl < System.nanoTime() -> {
                    logger.log { "TTL has expired, retrieve null" }
                    null
                }
                else -> {
                    logger.log { "Retrieve stored data: $cached" }
                    cached
                }
            }
        }
    }

    override fun cache(data: T) {
        setData(data)
    }

    private fun setData(data: T?) {
        return synchronized(lock) {
            storage.set(data)
            lastAccessTime.set(if (data == null) 0 else System.nanoTime())
        }
    }

    override fun clear() {
        setData(null)
    }

    companion object {

        /**
         * Create a new MemoryCacheStorage instance
         *
         * @param time time
         * @param unit unit of time
         * @param debug Debugging mode
         * @return [CacheStorage]
         */
        @JvmStatic
        @CheckResult
        @JvmOverloads
        fun <T : Any> create(
            time: Long,
            unit: TimeUnit,
            debug: Boolean = false
        ): CacheStorage<T> {
            return create(unit.toNanos(time), debug)
        }

        /**
         * Create a new MemoryCacheStorage instance
         *
         * @param ttl Time that cached data is valid in nanoseconds
         * @param debug Debugging mode
         * @return [CacheStorage]
         */
        @JvmStatic
        @CheckResult
        @JvmOverloads
        fun <T : Any> create(
            ttl: Long,
            debug: Boolean = false
        ): CacheStorage<T> {
            return MemoryCacheStorage(ttl, debug)
        }
    }
}
