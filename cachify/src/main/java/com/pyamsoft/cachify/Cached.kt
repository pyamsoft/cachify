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

/**
 * Cached data wrapper which resolves upstream data using 0 parameters
 */
interface Cached<R> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(): R

}

/**
 * Cached data wrapper which resolves upstream data using 1 parameter
 */
interface Cached1<R, T1> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(p1: T1): R

}

/**
 * Cached data wrapper which resolves upstream data using 2 parameters
 */
interface Cached2<R, T1, T2> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2
  ): R

}

/**
 * Cached data wrapper which resolves upstream data using 3 parameters
 */
interface Cached3<R, T1, T2, T3> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2,
    p3: T3
  ): R

}

/**
 * Cached data wrapper which resolves upstream data using 4 parameters
 */
interface Cached4<R, T1, T2, T3, T4> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4
  ): R

}

/**
 * Cached data wrapper which resolves upstream data using 5 parameters
 */
interface Cached5<R, T1, T2, T3, T4, T5> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5
  ): R

}

/**
 * Cached data wrapper which resolves upstream data using 6 parameters
 */
interface Cached6<R, T1, T2, T3, T4, T5, T6> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
    p6: T6
  ): R

}

/**
 * Cached data wrapper which resolves upstream data using 7 parameters
 */
interface Cached7<R, T1, T2, T3, T4, T5, T6, T7> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
    p6: T6,
    p7: T7
  ): R

}

/**
 * Cached data wrapper which resolves upstream data using 8 parameters
 */
interface Cached8<R, T1, T2, T3, T4, T5, T6, T7, T8> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
    p6: T6,
    p7: T7,
    p8: T8
  ): R

}

/**
 * Cached data wrapper which resolves upstream data using 9 parameters
 */
interface Cached9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> : Cache {

  /**
   * Get data either from cache or upstream
   */
  @CheckResult
  suspend fun call(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
    p6: T6,
    p7: T7,
    p8: T8,
    p9: T9
  ): R

}
