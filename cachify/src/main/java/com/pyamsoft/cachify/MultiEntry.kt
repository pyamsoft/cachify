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

@file:JvmMultifileClass
@file:JvmName("Cachify")

package com.pyamsoft.cachify

import androidx.annotation.CheckResult
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_TIME
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_UNIT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@CheckResult
@PublishedApi
internal fun <K : Any> K.tag(debugTag: String): String {
  return if (debugTag.isBlank()) "" else "$debugTag-$this"
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.() -> V
): MultiCached<K, V> {
  return object : MultiCached<K, V> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached.Caller<V>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached.Caller<V> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached.Caller<V> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())
            private val operation: suspend CoroutineScope.() -> V = { upstream(this) }

            override suspend fun call(): V {
              return conductor.cache(operation)
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1) -> V
): MultiCached1<K, V, T1> {
  return object : MultiCached1<K, V, T1> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached1.Caller<V, T1>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached1.Caller<V, T1> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached1.Caller<V, T1> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(p1: T1): V {
              return conductor.cache { upstream(p1) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2) -> V
): MultiCached2<K, V, T1, T2> {
  return object : MultiCached2<K, V, T1, T2> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached2.Caller<V, T1, T2>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached2.Caller<V, T1, T2> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached2.Caller<V, T1, T2> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(p1: T1, p2: T2): V {
              return conductor.cache { upstream(p1, p2) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3) -> V
): MultiCached3<K, V, T1, T2, T3> {
  return object : MultiCached3<K, V, T1, T2, T3> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached3.Caller<V, T1, T2, T3>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached3.Caller<V, T1, T2, T3> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached3.Caller<V, T1, T2, T3> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(p1: T1, p2: T2, p3: T3): V {
              return conductor.cache { upstream(p1, p2, p3) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> V
): MultiCached4<K, V, T1, T2, T3, T4> {
  return object : MultiCached4<K, V, T1, T2, T3, T4> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached4.Caller<V, T1, T2, T3, T4>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached4.Caller<V, T1, T2, T3, T4> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached4.Caller<V, T1, T2, T3, T4> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): V {
              return conductor.cache { upstream(p1, p2, p3, p4) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V
): MultiCached5<K, V, T1, T2, T3, T4, T5> {
  return object : MultiCached5<K, V, T1, T2, T3, T4, T5> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached5.Caller<V, T1, T2, T3, T4, T5>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached5.Caller<V, T1, T2, T3, T4, T5> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached5.Caller<V, T1, T2, T3, T4, T5> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): V {
              return conductor.cache { upstream(p1, p2, p3, p4, p5) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> V
): MultiCached6<K, V, T1, T2, T3, T4, T5, T6> {
  return object : MultiCached6<K, V, T1, T2, T3, T4, T5, T6> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached6.Caller<V, T1, T2, T3, T4, T5, T6>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached6.Caller<V, T1, T2, T3, T4, T5, T6> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached6.Caller<V, T1, T2, T3, T4, T5, T6> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6): V {
              return conductor.cache { upstream(p1, p2, p3, p4, p5, p6) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> V
): MultiCached7<K, V, T1, T2, T3, T4, T5, T6, T7> {
  return object : MultiCached7<K, V, T1, T2, T3, T4, T5, T6, T7> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached7.Caller<V, T1, T2, T3, T4, T5, T6, T7>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached7.Caller<V, T1, T2, T3, T4, T5, T6, T7> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached7.Caller<V, T1, T2, T3, T4, T5, T6, T7> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7): V {
              return conductor.cache { upstream(p1, p2, p3, p4, p5, p6, p7) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> V
): MultiCached8<K, V, T1, T2, T3, T4, T5, T6, T7, T8> {
  return object : MultiCached8<K, V, T1, T2, T3, T4, T5, T6, T7, T8> {

    private val mutex = Mutex()
    private val caches = mutableMapOf<K, MultiCached8.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached8.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached8.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
                p5: T5,
                p6: T6,
                p7: T7,
                p8: T8
            ): V {
              return conductor.cache { upstream(p1, p2, p3, p4, p5, p6, p7, p8) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> multiCachify(
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> V
): MultiCached9<K, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
  return object : MultiCached9<K, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {

    private val mutex = Mutex()
    private val caches =
        mutableMapOf<K, MultiCached9.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8, T9>>()

    override suspend fun clear() {
      mutex.withLock {
        caches.forEach { it.value.clear() }
        caches.clear()
      }
    }

    override suspend fun key(key: K): MultiCached9.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object : MultiCached9.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {

            private val conductor = CacheOperator.create(key.tag(debugTag), storage())

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
                p5: T5,
                p6: T6,
                p7: T7,
                p8: T8,
                p9: T9
            ): V {
              return conductor.cache { upstream(p1, p2, p3, p4, p5, p6, p7, p8, p9) }
            }

            override suspend fun clear() {
              conductor.clear()
            }
          }
        }
      }
    }
  }
}
