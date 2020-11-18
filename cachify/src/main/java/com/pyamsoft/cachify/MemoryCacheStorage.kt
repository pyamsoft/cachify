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
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * CacheStorage implementation which is backed by memory. Short lived cache.
 */
public class MemoryCacheStorage<K : Any, V : Any> internal constructor(
    private val ttl: Long
) : CacheStorage<K, V> {

    private val mutex = Mutex()
    private val storage = mutableMapOf<K, Data<V>?>()

    override suspend fun retrieve(key: K): V? {
        val cached: Data<V>? = mutex.withLock { storage[key] }
        return when {
            cached == null -> null
            cached.lastAccessTime + ttl < System.nanoTime() -> null
            else -> cached.data
        }
    }

    override suspend fun cache(key: K, data: V) {
        setData(key, data)
    }

    private suspend fun setData(key: K, data: V?) {
        val newData = if (data == null) null else Data(data, System.nanoTime())
        mutex.withLock {
            storage[key] = newData
        }
    }

    override suspend fun invalidate(key: K) {
        mutex.withLock {
            storage.remove(key)
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            storage.clear()
        }
    }

    private data class Data<T : Any>(val data: T, val lastAccessTime: Long)

    public companion object {

        /**
         * Create a new MemoryCacheStorage instance
         *
         * @param time time
         * @param unit unit of time
         * @return [CacheStorage]
         */
        @JvmStatic
        @CheckResult
        public fun <K : Any, V : Any> create(time: Long, unit: TimeUnit): CacheStorage<K, V> {
            return create(unit.toNanos(time))
        }

        /**
         * Create a new MemoryCacheStorage instance
         *
         * @param ttl Time that cached data is valid in nanoseconds
         * @return [CacheStorage]
         */
        @JvmStatic
        @CheckResult
        public fun <K : Any, V : Any> create(ttl: Long): CacheStorage<K, V> {
            return MemoryCacheStorage(ttl)
        }
    }
}
