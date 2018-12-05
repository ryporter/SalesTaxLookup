package com.ryanwporter.salestax;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Least Recently Used Cache.  That is, it kicks out the least recently used element
 * when a new element is added and the cache is at capacity.
 * 
 * Credit to user Hank Gay on http://stackoverflow.com/a/1953516
 * 
 * NB: Not Thread Safe
 */
public class LruCache<A, B> extends LinkedHashMap<A, B> {
    private static final long serialVersionUID = 1L;
    
    private final int maxEntries;

    public LruCache(final int maxEntries) {
        super(maxEntries + 1,
            0.75f, // load factor
            true); // access order
        this.maxEntries = maxEntries;
    }

    @Override protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
        return (super.size() > maxEntries);
    }
}
