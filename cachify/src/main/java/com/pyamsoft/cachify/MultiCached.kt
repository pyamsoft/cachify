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

package com.pyamsoft.cachify

import androidx.annotation.CheckResult

/**
 * Internal interface.
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface Keyed<K : Any, R : Cache> : Cache {

    /**
     * Return an instance of a caller which can get data from either cache or upstream
     */
    @CheckResult
    public suspend fun key(key: K): R
}

/**
 * Cached data wrapper which resolves upstream data using no parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached<K : Any, V : Any> : Keyed<K, MultiCached.Caller<V>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 1 parameter
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached1<K : Any, V : Any, T1> : Keyed<K, MultiCached1.Caller<V, T1>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 2 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached2<K : Any, V : Any, T1, T2> :
    Keyed<K, MultiCached2.Caller<V, T1, T2>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1, p2: T2): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 3 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached3<K : Any, V : Any, T1, T2, T3> :
    Keyed<K, MultiCached3.Caller<V, T1, T2, T3>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2, T3> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1, p2: T2, p3: T3): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 4 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached4<K : Any, V : Any, T1, T2, T3, T4> :
    Keyed<K, MultiCached4.Caller<V, T1, T2, T3, T4>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2, T3, T4> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 5 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached5<K : Any, V : Any, T1, T2, T3, T4, T5> :
    Keyed<K, MultiCached5.Caller<V, T1, T2, T3, T4, T5>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2, T3, T4, T5> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 6 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached6<K : Any, V : Any, T1, T2, T3, T4, T5, T6> :
    Keyed<K, MultiCached6.Caller<V, T1, T2, T3, T4, T5, T6>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2, T3, T4, T5, T6> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 7 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached7<K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7> :
    Keyed<K, MultiCached7.Caller<V, T1, T2, T3, T4, T5, T6, T7>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2, T3, T4, T5, T6, T7> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 8 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached8<K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8> :
    Keyed<K, MultiCached8.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2, T3, T4, T5, T6, T7, T8> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8): V
    }
}

/**
 * Cached data wrapper which resolves upstream data using 9 parameters
 *
 * Keys must be provided and can be anything that implements a valid equals() and hashCode()
 */
public interface MultiCached9<K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> :
    Keyed<K, MultiCached9.Caller<V, T1, T2, T3, T4, T5, T6, T7, T8, T9>> {

    /**
     * Caller interface for a multi-cache
     */
    public interface Caller<V : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> : Cache {

        /**
         * Get data either from cache or upstream
         */
        @CheckResult
        public suspend fun call(
            p1: T1,
            p2: T2,
            p3: T3,
            p4: T4,
            p5: T5,
            p6: T6,
            p7: T7,
            p8: T8,
            p9: T9
        ): V
    }
}
