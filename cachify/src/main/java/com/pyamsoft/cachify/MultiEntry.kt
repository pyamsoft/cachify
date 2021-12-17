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
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Create a debugTag for each multi-caller cache entry */
@CheckResult
@PublishedApi
internal fun <K : Any> K.tag(debugTag: String): String {
  return if (debugTag.isBlank()) "" else "$debugTag-$this"
}

/** Base class for a MultiCached object */
@PublishedApi
internal abstract class BaseMulti<K : Any, Caller : Cache>
protected constructor(
    private val context: CoroutineContext,
) : Cache {

  protected val mutex = Mutex()
  protected val caches = mutableMapOf<K, Caller>()

  final override suspend fun clear() =
      withContext(context = NonCancellable) {
        // Maybe we can simplify this with a withContext(context = NonCancellable + context)
        // but I don't know enough about Coroutines right now to figure out if that works
        // or if plussing the contexts will remove NonCancel, so here we go instead.
        withContext(context = context) {
          // Coroutine scope here to make sure if anything throws an error we catch it in the
          // scope
          mutex.withLock {
            caches.forEach { it.value.clear() }
            caches.clear()
          }
        }
      }
}

/** Base class for a MultiCached*.Caller object */
@PublishedApi
internal abstract class BaseMultiCaller<V : Any>
protected constructor(
    private val context: CoroutineContext,
    debugTag: String,
    storage: List<CacheStorage<V>>,
) : Cache {

  protected val conductor =
      CacheOperator.create(
          context,
          debugTag,
          storage,
      )

  final override suspend fun clear() =
      withContext(context = NonCancellable) {
        // Maybe we can simplify this with a withContext(context = NonCancellable +
        // context)
        // but I don't know enough about Coroutines right now to figure out if that works
        // or if plussing the contexts will remove NonCancel, so here we go instead.
        withContext(context = context) {
          // Coroutine scope here to make sure if anything throws an error we catch it in
          // the scope
          conductor.clear()
        }
      }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.() -> V,
): MultiCached<K, V> {
  return object : BaseMulti<K, MultiCached.Caller<V>>(context), MultiCached<K, V> {

    override suspend fun key(key: K): MultiCached.Caller<V> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()), MultiCached.Caller<V> {

            private val operation: suspend CoroutineScope.() -> V = { upstream(this) }

            override suspend fun call(): V = conductor.cache(operation)
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1) -> V
): MultiCached1<K, V, T1> {
  return object : BaseMulti<K, MultiCached1.Caller<V, T1>>(context), MultiCached1<K, V, T1> {

    override suspend fun key(key: K): MultiCached1.Caller<V, T1> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached1.Caller<V, T1> {

            override suspend fun call(p1: T1): V = conductor.cache { upstream(p1) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2) -> V
): MultiCached2<K, V, T1, T2> {
  return object :
      BaseMulti<K, MultiCached2.Caller<V, T1, T2>>(context), MultiCached2<K, V, T1, T2> {

    override suspend fun key(key: K): MultiCached2.Caller<V, T1, T2> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached2.Caller<V, T1, T2> {

            override suspend fun call(p1: T1, p2: T2): V = conductor.cache { upstream(p1, p2) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3) -> V
): MultiCached3<K, V, T1, T2, T3> {
  return object :
      BaseMulti<K, MultiCached3.Caller<V, T1, T2, T3>>(context), MultiCached3<K, V, T1, T2, T3> {

    override suspend fun key(key: K): MultiCached3.Caller<V, T1, T2, T3> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached3.Caller<V, T1, T2, T3> {

            override suspend fun call(p1: T1, p2: T2, p3: T3): V =
                conductor.cache { upstream(p1, p2, p3) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> V
): MultiCached4<K, V, T1, T2, T3, T4> {
  return object :
      BaseMulti<K, MultiCached4.Caller<V, T1, T2, T3, T4>>(context),
      MultiCached4<K, V, T1, T2, T3, T4> {

    override suspend fun key(key: K): MultiCached4.Caller<V, T1, T2, T3, T4> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached4.Caller<V, T1, T2, T3, T4> {

            override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): V =
                conductor.cache { upstream(p1, p2, p3, p4) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V
): MultiCached5<K, V, T1, T2, T3, T4, T5> {
  return object :
      BaseMulti<K, MultiCached5.Caller<V, T1, T2, T3, T4, T5>>(context),
      MultiCached5<K, V, T1, T2, T3, T4, T5> {

    override suspend fun key(key: K): MultiCached5.Caller<V, T1, T2, T3, T4, T5> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached5.Caller<V, T1, T2, T3, T4, T5> {

            override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): V =
                conductor.cache { upstream(p1, p2, p3, p4, p5) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("You probably shouldn't be making functions with over 5 parameters.")
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> V
): MultiCached6<K, V, T1, T2, T3, T4, T5, T6> {
  return object :
      BaseMulti<K, MultiCached6.Caller<V, T1, T2, T3, T4, T5, T6>>(context),
      MultiCached6<K, V, T1, T2, T3, T4, T5, T6> {

    override suspend fun key(key: K): MultiCached6.Caller<V, T1, T2, T3, T4, T5, T6> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached6.Caller<V, T1, T2, T3, T4, T5, T6> {

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
                p5: T5,
                p6: T6,
            ): V =
                conductor.cache {
                  upstream(
                      p1,
                      p2,
                      p3,
                      p4,
                      p5,
                      p6,
                  )
                }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("You probably shouldn't be making functions with over 5 parameters.")
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> V
): MultiCached7<K, V, T1, T2, T3, T4, T5, T6, T7> {
  return object :
      BaseMulti<K, MultiCached7.Caller<V, T1, T2, T3, T4, T5, T6, T7>>(context),
      MultiCached7<K, V, T1, T2, T3, T4, T5, T6, T7> {

    override suspend fun key(key: K): MultiCached7.Caller<V, T1, T2, T3, T4, T5, T6, T7> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached7.Caller<V, T1, T2, T3, T4, T5, T6, T7> {

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
                p5: T5,
                p6: T6,
                p7: T7,
            ): V =
                conductor.cache {
                  upstream(
                      p1,
                      p2,
                      p3,
                      p4,
                      p5,
                      p6,
                      p7,
                  )
                }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("You probably shouldn't be making functions with over 5 parameters.")
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> V
): MultiCached8<K, V, T1, T2, T3, T4, T5, T6, T7, T8> {
  return object :
      BaseMulti<K, MultiCached8.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8>>(context),
      MultiCached8<K, V, T1, T2, T3, T4, T5, T6, T7, T8> {

    override suspend fun key(key: K): MultiCached8.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached8.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8> {

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
                p5: T5,
                p6: T6,
                p7: T7,
                p8: T8,
            ): V =
                conductor.cache {
                  upstream(
                      p1,
                      p2,
                      p3,
                      p4,
                      p5,
                      p6,
                      p7,
                      p8,
                  )
                }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("You probably shouldn't be making functions with over 5 parameters.")
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> V
): MultiCached9<K, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
  return object :
      BaseMulti<K, MultiCached9.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8, T9>>(context),
      MultiCached9<K, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {

    override suspend fun key(key: K): MultiCached9.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              BaseMultiCaller<V>(context, key.tag(debugTag), storage()),
              MultiCached9.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
                p5: T5,
                p6: T6,
                p7: T7,
                p8: T8,
                p9: T9,
            ): V =
                conductor.cache {
                  upstream(
                      p1,
                      p2,
                      p3,
                      p4,
                      p5,
                      p6,
                      p7,
                      p8,
                      p9,
                  )
                }
          }
        }
      }
    }
  }
}
