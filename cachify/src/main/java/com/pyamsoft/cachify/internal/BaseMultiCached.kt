package com.pyamsoft.cachify.internal

import com.pyamsoft.cachify.Cache
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/** Base class for a MultiCached object */
@PublishedApi
internal abstract class BaseMultiCached<K : Any, Caller : Cache>
protected constructor(
    private val context: CoroutineContext,
) : Cache {

  // Don't use protected to avoid exposing to public API
  internal val mutex = Mutex()

  // Don't use protected to avoid exposing to public API
  internal val caches = mutableMapOf<K, Caller>()

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