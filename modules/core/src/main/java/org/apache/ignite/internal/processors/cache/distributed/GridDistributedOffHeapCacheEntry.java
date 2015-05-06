package org.apache.ignite.internal.processors.cache.distributed;

import org.apache.ignite.internal.processors.cache.*;

/**
 * Entry for distributed (replicated/partitioned) cache in off-heap tiered/off-heap values memory mode.
 */
public class GridDistributedOffHeapCacheEntry extends GridDistributedCacheEntry {
    /** Off-heap value pointer. */
    protected long valPtr;

    /**
     * @param ctx   Cache context.
     * @param key   Cache key.
     * @param hash  Key hash value.
     * @param val   Entry value.
     * @param next  Next entry in the linked list.
     * @param hdrId Cache map header ID.
     */
    public GridDistributedOffHeapCacheEntry(GridCacheContext ctx, KeyCacheObject key, int hash, CacheObject val, GridCacheMapEntry next, int hdrId) {
        super(ctx, key, hash, val, next, hdrId);
    }

    /** {@inheritDoc} */
    @Override protected boolean hasValPtr() {
        return valPtr != 0;
    }

    /** {@inheritDoc} */
    @Override protected long valPtr() {
        return valPtr;
    }

    /** {@inheritDoc} */
    @Override protected void setValPtr(long valPtr) {
        this.valPtr = valPtr;
    }
}
