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

@file:JvmMultifileClass
@file:JvmName("Cachify")

package com.pyamsoft.cachify

import androidx.annotation.CheckResult
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_TIME
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_UNIT
import com.pyamsoft.cachify.internal.BaseCacheCaller
import com.pyamsoft.cachify.storage.CacheStorage
import com.pyamsoft.cachify.storage.MemoryCacheStorage
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <R : Any> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.() -> R
): Cached<R> {
  return object :
      Cached<R>,
      BaseCacheCaller<R>(
          context = context,
          debugTag = debugTag,
          storage = storage(),
      ) {

    private val operation: suspend CoroutineScope.() -> R = { upstream(this) }

    override suspend fun call(): R =
        withContext(context = context) { orchestrator.fetch(operation) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("""Unexpected API behavior

The cachify{} construct with parameters had an unexpected implementation.
When parameters changed, the upstream would not be fetched with the new parameters if
a previous result was still valid in the cache - rather that old stale data using old parameters
was returned to the caller.

While potentially useful in some very niche cases, this behavior was largely unexpected.

Users hoping for a more regular behavior are encouraged to migrate over to multiCachify{}
calls. The multiCachify{} construct takes a Key parameter and handles a unique cache instance
per unique key. As long as parameters that construct the Key are deterministic and unchanged,
the same result will continue to be cached and returned. Changes to parameters which construct
the Key return different results as one would expect.
""",
)
public inline fun <R : Any, T1> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1) -> R
): Cached1<R, T1> {
  return object :
      Cached1<R, T1>,
      BaseCacheCaller<R>(
          context = context,
          debugTag = debugTag,
          storage = storage(),
      ) {

    override suspend fun call(p1: T1): R = orchestrator.fetch { upstream(p1) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("""Unexpected API behavior

The cachify{} construct with parameters had an unexpected implementation.
When parameters changed, the upstream would not be fetched with the new parameters if
a previous result was still valid in the cache - rather that old stale data using old parameters
was returned to the caller.

While potentially useful in some very niche cases, this behavior was largely unexpected.

Users hoping for a more regular behavior are encouraged to migrate over to multiCachify{}
calls. The multiCachify{} construct takes a Key parameter and handles a unique cache instance
per unique key. As long as parameters that construct the Key are deterministic and unchanged,
the same result will continue to be cached and returned. Changes to parameters which construct
the Key return different results as one would expect.
""",
)
public inline fun <R : Any, T1, T2> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2) -> R
): Cached2<R, T1, T2> {
  return object :
      Cached2<R, T1, T2>,
      BaseCacheCaller<R>(
          context = context,
          debugTag = debugTag,
          storage = storage(),
      ) {

    override suspend fun call(
        p1: T1,
        p2: T2,
    ): R = orchestrator.fetch { upstream(p1, p2) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("""Unexpected API behavior

The cachify{} construct with parameters had an unexpected implementation.
When parameters changed, the upstream would not be fetched with the new parameters if
a previous result was still valid in the cache - rather that old stale data using old parameters
was returned to the caller.

While potentially useful in some very niche cases, this behavior was largely unexpected.

Users hoping for a more regular behavior are encouraged to migrate over to multiCachify{}
calls. The multiCachify{} construct takes a Key parameter and handles a unique cache instance
per unique key. As long as parameters that construct the Key are deterministic and unchanged,
the same result will continue to be cached and returned. Changes to parameters which construct
the Key return different results as one would expect.
""",
)
public inline fun <R : Any, T1, T2, T3> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3) -> R
): Cached3<R, T1, T2, T3> {
  return object :
      Cached3<R, T1, T2, T3>,
      BaseCacheCaller<R>(
          context = context,
          debugTag = debugTag,
          storage = storage(),
      ) {

    override suspend fun call(
        p1: T1,
        p2: T2,
        p3: T3,
    ): R = orchestrator.fetch { upstream(p1, p2, p3) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("""Unexpected API behavior

The cachify{} construct with parameters had an unexpected implementation.
When parameters changed, the upstream would not be fetched with the new parameters if
a previous result was still valid in the cache - rather that old stale data using old parameters
was returned to the caller.

While potentially useful in some very niche cases, this behavior was largely unexpected.

Users hoping for a more regular behavior are encouraged to migrate over to multiCachify{}
calls. The multiCachify{} construct takes a Key parameter and handles a unique cache instance
per unique key. As long as parameters that construct the Key are deterministic and unchanged,
the same result will continue to be cached and returned. Changes to parameters which construct
the Key return different results as one would expect.
""",
)
public inline fun <R : Any, T1, T2, T3, T4> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> R
): Cached4<R, T1, T2, T3, T4> {
  return object :
      Cached4<R, T1, T2, T3, T4>,
      BaseCacheCaller<R>(
          context = context,
          debugTag = debugTag,
          storage = storage(),
      ) {

    override suspend fun call(
        p1: T1,
        p2: T2,
        p3: T3,
        p4: T4,
    ): R = orchestrator.fetch { upstream(p1, p2, p3, p4) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
@Deprecated("""Unexpected API behavior

The cachify{} construct with parameters had an unexpected implementation.
When parameters changed, the upstream would not be fetched with the new parameters if
a previous result was still valid in the cache - rather that old stale data using old parameters
was returned to the caller.

While potentially useful in some very niche cases, this behavior was largely unexpected.

Users hoping for a more regular behavior are encouraged to migrate over to multiCachify{}
calls. The multiCachify{} construct takes a Key parameter and handles a unique cache instance
per unique key. As long as parameters that construct the Key are deterministic and unchanged,
the same result will continue to be cached and returned. Changes to parameters which construct
the Key return different results as one would expect.
""",
)
public inline fun <R : Any, T1, T2, T3, T4, T5> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> R
): Cached5<R, T1, T2, T3, T4, T5> {
  return object :
      Cached5<R, T1, T2, T3, T4, T5>,
      BaseCacheCaller<R>(
          context = context,
          debugTag = debugTag,
          storage = storage(),
      ) {

    override suspend fun call(
        p1: T1,
        p2: T2,
        p3: T3,
        p4: T4,
        p5: T5,
    ): R = orchestrator.fetch { upstream(p1, p2, p3, p4, p5) }
  }
}
