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

package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.cache.distributed.dht.atomic.*;
import org.apache.ignite.internal.processors.cache.distributed.dht.colocated.*;
import org.apache.ignite.internal.processors.cache.distributed.near.*;
import org.apache.ignite.internal.processors.cache.local.*;

/**
 * Cache map entry self test.
 */
public class CacheMapEntrySelfTest extends GridCacheAbstractSelfTest {
    /** Cache id. */
    private int cacheId = 0;

    /** {@inheritDoc} */
    @Override protected int gridCount() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        stopAllGrids();
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        startGrids(1);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        // No-op.
    }

    /**
     * @param gridName Grid name.
     * @param memoryMode Memory mode.
     * @param atomicityMode Atomicity mode.
     * @param cacheMode Cache mode.
     * @param cacheName Cache name.
     * @return Cache configuration.
     * @throws Exception If failed.
     */
    private CacheConfiguration cacheConfiguration(String gridName, CacheMemoryMode memoryMode,
        CacheAtomicityMode atomicityMode, CacheMode cacheMode, String cacheName) throws Exception {
        CacheConfiguration cfg = super.cacheConfiguration(gridName);

        cfg.setCacheMode(cacheMode);
        cfg.setAtomicityMode(atomicityMode);
        cfg.setMemoryMode(memoryMode);
        cfg.setName(cacheName);

        return cfg;
    }

    /**
     * @throws Exception If failed.
     */
    public void testCacheMapEntry() throws Exception {
        checkCacheMapEntry(CacheMemoryMode.ONHEAP_TIERED, CacheAtomicityMode.ATOMIC, CacheMode.LOCAL,
            GridLocalCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_TIERED, CacheAtomicityMode.ATOMIC, CacheMode.LOCAL,
            GridLocalCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_VALUES, CacheAtomicityMode.ATOMIC, CacheMode.LOCAL,
            GridLocalCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.ONHEAP_TIERED, CacheAtomicityMode.TRANSACTIONAL, CacheMode.LOCAL,
            GridLocalCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_TIERED, CacheAtomicityMode.TRANSACTIONAL, CacheMode.LOCAL,
            GridLocalCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_VALUES, CacheAtomicityMode.TRANSACTIONAL, CacheMode.LOCAL,
            GridLocalCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.ONHEAP_TIERED, CacheAtomicityMode.ATOMIC, CacheMode.PARTITIONED,
            GridNearCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_TIERED, CacheAtomicityMode.ATOMIC, CacheMode.PARTITIONED,
            GridNearOffHeapCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_VALUES, CacheAtomicityMode.ATOMIC, CacheMode.PARTITIONED,
            GridNearOffHeapCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.ONHEAP_TIERED, CacheAtomicityMode.TRANSACTIONAL, CacheMode.PARTITIONED,
            GridNearCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_TIERED, CacheAtomicityMode.TRANSACTIONAL, CacheMode.PARTITIONED,
            GridNearOffHeapCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_VALUES, CacheAtomicityMode.TRANSACTIONAL, CacheMode.PARTITIONED,
            GridNearOffHeapCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.ONHEAP_TIERED, CacheAtomicityMode.ATOMIC, CacheMode.REPLICATED,
            GridDhtAtomicCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_TIERED, CacheAtomicityMode.ATOMIC, CacheMode.REPLICATED,
            GridDhtAtomicOffHeapCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_VALUES, CacheAtomicityMode.ATOMIC, CacheMode.REPLICATED,
            GridDhtAtomicOffHeapCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.ONHEAP_TIERED, CacheAtomicityMode.TRANSACTIONAL, CacheMode.REPLICATED,
            GridDhtColocatedCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_TIERED, CacheAtomicityMode.TRANSACTIONAL, CacheMode.REPLICATED,
            GridDhtColocatedOffHeapCacheEntry.class);
        checkCacheMapEntry(CacheMemoryMode.OFFHEAP_VALUES, CacheAtomicityMode.TRANSACTIONAL, CacheMode.REPLICATED,
            GridDhtColocatedOffHeapCacheEntry.class);
    }

    /**
     * @param memoryMode Cache memory mode.
     * @param atomicityMode Cache atomicity mode.
     * @param cacheMode Cache mode.
     * @param entryClass Class of cache map entry.
     * @throws Exception If failed.
     */
    private void checkCacheMapEntry(CacheMemoryMode memoryMode, CacheAtomicityMode atomicityMode, CacheMode cacheMode,
        Class<?> entryClass) throws Exception {
        CacheConfiguration cfg = cacheConfiguration(grid(0).name(), memoryMode, atomicityMode, cacheMode,
            "Cache" + cacheId++);

        IgniteCache jcache = grid(0).getOrCreateCache(cfg);

        GridCacheAdapter<Integer, String> cache = ((IgniteKernal)grid(0)).internalCache(jcache.getName());

        Integer key = primaryKey(grid(0).cache(null));

        cache.put(key, "val");

        GridCacheEntryEx entry = cache.entryEx(key);

        entry.unswap(true);

        assertNotNull(entry);

        assertEquals(entry.getClass(), entryClass);
    }
}
