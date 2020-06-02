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
 *
 * Keys are stored as random strings
 */
interface Cached<R : Any> : Cache<String> {

    /**
     * Get data either from cache or upstream
     */
    @CheckResult
    suspend fun call(): R
}

/**
 * Cached data wrapper which resolves upstream data using 1 parameter
 *
 * Keys are stored as random strings
 */
interface Cached1<R : Any, T1> : Cache<String> {

    /**
     * Get data either from cache or upstream
     */
    @CheckResult
    suspend fun call(p1: T1): R
}

/**
 * Cached data wrapper which resolves upstream data using 2 parameters
 *
 * Keys are stored as random strings
 */
interface Cached2<R : Any, T1, T2> : Cache<String> {

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
 *
 * Keys are stored as random strings
 */
interface Cached3<R : Any, T1, T2, T3> : Cache<String> {

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
 *
 * Keys are stored as random strings
 */
interface Cached4<R : Any, T1, T2, T3, T4> : Cache<String> {

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
 *
 * Keys are stored as random strings
 */
interface Cached5<R : Any, T1, T2, T3, T4, T5> : Cache<String> {

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
 *
 * Keys are stored as random strings
 */
interface Cached6<R : Any, T1, T2, T3, T4, T5, T6> : Cache<String> {

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
 *
 * Keys are stored as random strings
 */
interface Cached7<R : Any, T1, T2, T3, T4, T5, T6, T7> : Cache<String> {

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
 *
 * Keys are stored as random strings
 */
interface Cached8<R : Any, T1, T2, T3, T4, T5, T6, T7, T8> : Cache<String> {

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
 *
 * Keys are stored as random strings
 */
interface Cached9<R : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> : Cache<String> {

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
