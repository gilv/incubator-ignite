/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.util;

import org.jsr166.*;

import java.util.concurrent.*;

/**
 * Cache for enum constants.
 */
public class GridEnumCache {
    /** Cache for enum constants. */
    private static final ConcurrentMap<Class<?>, Object[]> ENUM_CACHE = new ConcurrentHashMap8<>();

    /**
     * Gets enum constants for provided class.
     *
     * @param cls Class.
     * @return Enum constants.
     */
    public static Object[] get(Class<?> cls) {
        assert cls != null;

        Object[] vals = ENUM_CACHE.get(cls);

        if (vals == null) {
            vals = cls.getEnumConstants();

            ENUM_CACHE.putIfAbsent(cls, vals);
        }

        return vals;
    }

    /**
     * Clears cache.
     */
    public static void clear() {
        ENUM_CACHE.clear();
    }
}
