/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.cachify.storage

import androidx.annotation.CheckResult
import androidx.annotation.VisibleForTesting
import java.time.Clock
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** CacheStorage implementation which is backed by memory. Short lived cache. */
public class MemoryCacheStorage<T : Any>
internal constructor(
    private val ttl: Long,
    private val clock: Clock,
) : CacheStorage<T> {

  private val mutex = Mutex()
  private val storage = AtomicReference<Data<T>?>(null)

  override suspend fun retrieve(): T? {
    val cached: Data<T>? = mutex.withLock { storage.get() }
    return when {
      cached == null -> null
      cached.lastAccessTime.plusNanos(ttl) < LocalDateTime.now(clock) -> null
      else -> cached.data
    }
  }

  override suspend fun cache(data: T) {
    setData(data)
  }

  private suspend fun setData(data: T?) {
    val newData = if (data == null) null else Data(data, LocalDateTime.now(clock))
    mutex.withLock { storage.set(newData) }
  }

  override suspend fun clear() {
    setData(null)
  }

  private data class Data<T : Any>(
      val data: T,
      val lastAccessTime: LocalDateTime,
  )

  public companion object {

    /**
     * Create a new MemoryCacheStorage instance
     *
     * @param ttl Time that cached data is valid in nanoseconds
     * @param clock Time Clock to determine current times from
     * @return [CacheStorage]
     */
    @JvmStatic
    @CheckResult
    private fun <T : Any> make(
        ttl: Long,
        clock: Clock,
    ): CacheStorage<T> {
      return MemoryCacheStorage(ttl, clock)
    }

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
      return create(ttl = unit.toNanos(time))
    }

    /**
     * Create a new MemoryCacheStorage instance
     *
     * @param duration duration
     * @return [CacheStorage]
     */
    @JvmStatic
    @CheckResult
    public fun <T : Any> create(duration: Duration): CacheStorage<T> {
      return create(ttl = duration.inWholeNanoseconds)
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
      return make(
          ttl = ttl,
          clock = Clock.systemDefaultZone(),
      )
    }

    /**
     * Create a new MemoryCacheStorage instance
     *
     * @param duration duration
     * @return [CacheStorage]
     */
    @JvmStatic
    @CheckResult
    @VisibleForTesting
    internal fun <T : Any> createTest(duration: Duration, clock: Clock): CacheStorage<T> {
      return make(
          ttl = duration.inWholeNanoseconds,
          clock = clock,
      )
    }
  }
}
