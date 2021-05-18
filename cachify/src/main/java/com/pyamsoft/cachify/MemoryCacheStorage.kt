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
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** CacheStorage implementation which is backed by memory. Short lived cache. */
public class MemoryCacheStorage<T : Any> internal constructor(private val ttl: Long) :
    CacheStorage<T> {

  private val mutex = Mutex()
  private val storage = AtomicReference<Data<T>?>(null)

  override suspend fun retrieve(): T? {
    val cached: Data<T>? = mutex.withLock { storage.get() }
    return when {
      cached == null -> null
      cached.lastAccessTime + ttl < System.nanoTime() -> null
      else -> cached.data
    }
  }

  override suspend fun cache(data: T) {
    setData(data)
  }

  private suspend fun setData(data: T?) {
    val newData = if (data == null) null else Data(data, System.nanoTime())
    mutex.withLock { storage.set(newData) }
  }

  override suspend fun clear() {
    setData(null)
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
    public fun <T : Any> create(time: Long, unit: TimeUnit): CacheStorage<T> {
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
    public fun <T : Any> create(ttl: Long): CacheStorage<T> {
      return MemoryCacheStorage(ttl)
    }
  }
}
