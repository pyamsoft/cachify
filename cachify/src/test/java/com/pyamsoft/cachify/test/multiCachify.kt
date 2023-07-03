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

package com.pyamsoft.cachify.test

import com.pyamsoft.cachify.CachifyDefaults
import com.pyamsoft.cachify.env.TestClock
import com.pyamsoft.cachify.multiCachify
import com.pyamsoft.cachify.storage.MemoryCacheStorage
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest

public class MultiCachifyTest {

  @Test
  public fun defaults_Create(): Unit = runTest {
    val counter = AtomicInteger(0)

    // Creation does not throw
    val c = multiCachify<Int, Int> { counter.incrementAndGet() }
    assertNotNull(c)

    // Creation does not hit upstream
    assertEquals(counter.get(), 0)
  }

  @Test
  public fun defaults_Cache(): Unit = runTest {
    val counter = AtomicInteger(0)

    val c = multiCachify<Int, Int> { counter.getAndIncrement() }

    // Hit the upstream once
    val shouldBeZero = c.key(0).call()
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.key(0).call()
    assertEquals(shouldStillBeZero, 0)
  }

  @Test
  public fun defaults_ClearResets(): Unit = runTest {
    val counter = AtomicInteger(0)

    val c = multiCachify<Int, Int> { counter.getAndIncrement() }

    // Hit the upstream once
    val shouldBeZero = c.key(0).call()
    assertEquals(shouldBeZero, 0)

    c.clear()

    // Hit again should change
    val shouldBeOne = c.key(0).call()
    assertEquals(shouldBeOne, 1)
  }

  @Test
  public fun defaults_CacheTimeout(): Unit = runTest {
    val clock = TestClock.create()
    val counter = AtomicInteger(0)

    val c =
        multiCachify<Int, Int>(
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
    val shouldBeZero = c.key(0).call()
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.key(0).call()
    assertEquals(shouldStillBeZero, 0)

    // Advance the clock to invalidate the cache
    clock.setTime(Instant.now().plusSeconds(CachifyDefaults.DEFAULT_DURATION.inWholeSeconds * 2))

    // Hit again should change
    val shouldBeOne = c.key(0).call()
    assertEquals(shouldBeOne, 1)

    // Hit again should not change
    val shouldStillBeOne = c.key(0).call()
    assertEquals(shouldStillBeOne, 1)
  }

  @Test
  public fun multiStorage_SelectionProcess(): Unit = runTest {
    val clock = TestClock.create()
    val counter = AtomicInteger(0)

    val c =
        multiCachify<Int, Int>(
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
    val shouldBeZero = c.key(0).call()
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.key(0).call()
    assertEquals(shouldStillBeZero, 0)

    // Advance the clock to invalidate the first memory cache storage
    // The second should still be valid though
    clock.setTime(Instant.now().plusSeconds(1))

    // Hit again should change
    val shouldStillStillBeZero = c.key(0).call()
    assertEquals(shouldStillStillBeZero, 0)

    // Advance the clock to invalidate the cache
    clock.setTime(Instant.now().plusSeconds(CachifyDefaults.DEFAULT_DURATION.inWholeSeconds * 2))

    // Hit again should change
    val shouldBeOne = c.key(0).call()
    assertEquals(shouldBeOne, 1)

    // Hit again should not change
    val shouldStillBeOne = c.key(0).call()
    assertEquals(shouldStillBeOne, 1)
  }

  @Test
  public fun key_Cache(): Unit = runTest {
    val counter0 = AtomicInteger(0)
    val counter1 = AtomicInteger(0)

    val c =
        multiCachify<Int, Int, Int> {
          if (it == 0) {
            counter0.getAndIncrement()
          } else {
            counter1.getAndIncrement()
          }
        }

    // Hit the upstream once
    val shouldBeZero = c.key(0).call(0)
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.key(1).call(1)
    assertEquals(shouldStillBeZero, 0)
  }

  @Test
  public fun key_Invalidate(): Unit = runTest {
    val counter0 = AtomicInteger(0)
    val counter1 = AtomicInteger(0)

    val c =
        multiCachify<Int, Int, Int> {
          if (it == 0) {
            counter0.getAndIncrement()
          } else {
            counter1.getAndIncrement()
          }
        }

    // Hit the upstream once
    val shouldBeZero = c.key(0).call(0)
    assertEquals(shouldBeZero, 0)

    // Hit again should not change
    val shouldStillBeZero = c.key(1).call(1)
    assertEquals(shouldStillBeZero, 0)

    // Invalidate one key but not the other
    c.key(0).clear()

    val shouldBeOne = c.key(0).call(0)
    assertEquals(shouldBeOne, 1)

    // Hit again should not change
    val shouldStillStillBeZero = c.key(1).call(1)
    assertEquals(shouldStillStillBeZero, 0)
  }
}
