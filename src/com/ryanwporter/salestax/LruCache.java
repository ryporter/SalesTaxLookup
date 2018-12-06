package com.ryanwporter.salestax;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * NOTE: This was my original implementation when I misunderstood the problem.
 * I'm leaving in, because it is what I would use in production.  
 * The implementation actually used for this problem is MyLruCache.
 * 
 * Least Recently Used Cache.  That is, it kicks out the least recently used element
 * when a new element is added and the cache is at capacity.
 * 
 * Credit to user Hank Gay on http://stackoverflow.com/a/1953516
 * 
 * NB: Not Thread Safe
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1L;
    
    private final int maxEntries;

    public LruCache(final int maxEntries) {
        super(maxEntries + 1,
            0.75f, // load factor
            true); // access order
        this.maxEntries = maxEntries;
    }

    @Override protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return (super.size() > maxEntries);
    }
}
