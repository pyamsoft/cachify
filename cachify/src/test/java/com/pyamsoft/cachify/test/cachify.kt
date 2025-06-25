/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.cachify.test

import com.pyamsoft.cachify.CachifyDefaults
import com.pyamsoft.cachify.cachify
import com.pyamsoft.cachify.env.TestClock
import com.pyamsoft.cachify.storage.MemoryCacheStorage
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

public class CachifyTest {

  @Test
  public fun defaults_Create(): Unit = runTest {
    val counter = AtomicInteger(0)

    // Creation does not throw
    val c = cachify { counter.incrementAndGet() }
    assertNotNull(c)

    // Creation does not hit upstream
    assertEquals(counter.get(), 0)
  }

  @Test
  public fun defaults_Cache(): Unit = runTest {
    val counter = AtomicInteger(0)

    val c = cachify { counter.getAndIncrement() }

    // Hit the upstream once
    val shouldBeZero = c.call()
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.call()
    assertEquals(shouldStillBeZero, 0)
  }

  @Test
  public fun defaults_ClearResets(): Unit = runTest {
    val counter = AtomicInteger(0)

    val c = cachify { counter.getAndIncrement() }

    // Hit the upstream once
    val shouldBeZero = c.call()
    assertEquals(shouldBeZero, 0)

    c.clear()

    // Hit again should change
    val shouldBeOne = c.call()
    assertEquals(shouldBeOne, 1)
  }

  @Test
  public fun defaults_CacheTimeout(): Unit = runTest {
    val clock = TestClock.create()
    val counter = AtomicInteger(0)

    val c =
        cachify(
            storage = {
              listOf(
                  MemoryCacheStorage.createTest(
                      duration = CachifyDefaults.DEFAULT_DURATION,
                      clock = clock,
                  ),
              )
            },
        ) {
          counter.getAndIncrement()
        }

    // Hit the upstream once
    val shouldBeZero = c.call()
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.call()
    assertEquals(shouldStillBeZero, 0)

    // Advance the clock to invalidate the cache
    clock.setTime(Instant.now().plusSeconds(CachifyDefaults.DEFAULT_DURATION.inWholeSeconds * 2))

    // Hit again should change
    val shouldBeOne = c.call()
    assertEquals(shouldBeOne, 1)

    // Hit again should not change
    val shouldStillBeOne = c.call()
    assertEquals(shouldStillBeOne, 1)
  }

  @Test
  public fun defaults_AttachInFlight(): Unit = runTest {
    val clock = TestClock.create()
    val counter = AtomicInteger(0)

    val c =
        cachify(
            storage = {
              listOf(
                  MemoryCacheStorage.createTest(
                      duration = CachifyDefaults.DEFAULT_DURATION,
                      clock = clock,
                  ),
              )
            },
        ) {
          delay(CachifyDefaults.DEFAULT_DURATION / 2)
          counter.getAndIncrement()
        }

    // Hit once, hit again while first is in-flight, should not change
    val (shouldBeZero, shouldStillBeZero) = awaitAll(async { c.call() }, async { c.call() })
    assertEquals(shouldBeZero, 0)
    assertEquals(shouldStillBeZero, 0)

    // Advance the clock to invalidate the cache
    clock.setTime(Instant.now().plusSeconds(CachifyDefaults.DEFAULT_DURATION.inWholeSeconds * 2))

    // Hit again should change, hit again while first is in-flight, should not change
    val (shouldBeOne, shouldStillBeOne) = awaitAll(async { c.call() }, async { c.call() })
    assertEquals(shouldBeOne, 1)
    assertEquals(shouldStillBeOne, 1)
  }

  @Test
  public fun multiStorage_SelectionProcess(): Unit = runTest {
    val clock = TestClock.create()
    val counter = AtomicInteger(0)

    val c =
        cachify(
            storage = {
              listOf(
                  MemoryCacheStorage.createTest(
                      duration = 1.seconds,
                      clock = clock,
                  ),
                  MemoryCacheStorage.createTest(
                      duration = CachifyDefaults.DEFAULT_DURATION,
                      clock = clock,
                  ),
              )
            },
        ) {
          counter.getAndIncrement()
        }

    // Hit the upstream once
    val shouldBeZero = c.call()
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.call()
    assertEquals(shouldStillBeZero, 0)

    // Advance the clock to invalidate the first memory cache storage
    // The second should still be valid though
    clock.setTime(Instant.now().plusSeconds(1))

    // Hit again should change
    val shouldStillStillBeZero = c.call()
    assertEquals(shouldStillStillBeZero, 0)

    // Advance the clock to invalidate the cache
    clock.setTime(Instant.now().plusSeconds(CachifyDefaults.DEFAULT_DURATION.inWholeSeconds * 2))

    // Hit again should change
    val shouldBeOne = c.call()
    assertEquals(shouldBeOne, 1)

    // Hit again should not change
    val shouldStillBeOne = c.call()
    assertEquals(shouldStillBeOne, 1)
  }
}
