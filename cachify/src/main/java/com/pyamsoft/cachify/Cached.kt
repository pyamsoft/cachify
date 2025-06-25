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

package com.pyamsoft.cachify

import androidx.annotation.CheckResult

/**
 * Cached data wrapper which resolves upstream data using 0 parameters
 *
 * Keys are stored as random strings
 */
public interface Cached<R> : Cache {

  /** Get data either from cache or upstream */
  @CheckResult public suspend fun call(): R
}

/**
 * Cached data wrapper which resolves upstream data using 1 parameter
 *
 * Keys are stored as random strings
 */
public interface Cached1<R, T1> : Cache {

  /** Get data either from cache or upstream */
  @CheckResult public suspend fun call(p1: T1): R
}

/**
 * Cached data wrapper which resolves upstream data using 2 parameters
 *
 * Keys are stored as random strings
 */
public interface Cached2<R, T1, T2> : Cache {

  /** Get data either from cache or upstream */
  @CheckResult public suspend fun call(p1: T1, p2: T2): R
}

/**
 * Cached data wrapper which resolves upstream data using 3 parameters
 *
 * Keys are stored as random strings
 */
public interface Cached3<R, T1, T2, T3> : Cache {

  /** Get data either from cache or upstream */
  @CheckResult public suspend fun call(p1: T1, p2: T2, p3: T3): R
}

/**
 * Cached data wrapper which resolves upstream data using 4 parameters
 *
 * Keys are stored as random strings
 */
public interface Cached4<R, T1, T2, T3, T4> : Cache {

  /** Get data either from cache or upstream */
  @CheckResult public suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): R
}

/**
 * Cached data wrapper which resolves upstream data using 5 parameters
 *
 * Keys are stored as random strings
 */
public interface Cached5<R, T1, T2, T3, T4, T5> : Cache {

  /** Get data either from cache or upstream */
  @CheckResult public suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): R
}
