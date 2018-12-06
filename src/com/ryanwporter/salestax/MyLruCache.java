package com.ryanwporter.salestax;

/*
 * Least Recently Used Cache.  That is, it kicks out the least recently used element
 * when a new element is added and the cache is at capacity.
 * 
 * For the purpose of this exercise, it makes no use of Java Collections.
 * 
 * Internally, it uses a pair of data structures:
 * 
 *   (1) An array of singly-linked lists, where each cell (or, bucket) contains entries for
 *       which the key hashes to its respective index
 *   (2) A doubly-linked list to record access order 
 * 
 * NB: Not Thread Safe
 */
public class MyLruCache<K, V> {
    
    private final int maxEntries;
    private final int numBuckets;
    
    private int numEntries = 0;
    
    private final CacheEntry<K,V>[] buckets;
    
    private int getBucketIdx(K key) {
        return Math.abs(key.hashCode()) % numBuckets;
    }
    
    private CacheEntry<K,V> firstAccessed = null;
    private CacheEntry<K,V> lastAccessed = null;

    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    
    public MyLruCache(final int maxEntries) {
        this(maxEntries, DEFAULT_LOAD_FACTOR);
    }

    public MyLruCache(final int maxEntries, final double loadFactor) {
        assert maxEntries > 0;
        assert loadFactor > 0;
        assert loadFactor <= 1;
        this.maxEntries = maxEntries;
        
        // This implementation initializes the array to its full size upfront,
        // both for simplicity and because it will quickly become full anyway
        // in the current use.  A library implementation would start smaller
        // and include rehashing logic based on the load factor.
        this.numBuckets = (int) Math.ceil(maxEntries / loadFactor); 
        this.buckets = new CacheEntry[numBuckets];
    }
    
    public int size() {
        return numEntries;
    }

    public boolean isEmpty() {
        return numEntries == 0;
    }

    public V get(K key) {
        return get(key, true);
    }
    
    // package protected method with ugly name, because it is just for use in tests
    V getWithoutRecordAccess(K key) {
        return get(key, false);
    }
    
    private V get(K key, boolean recordAccess) {
        final int bucket = getBucketIdx(key);
        
        for (CacheEntry<K,V> curr = buckets[bucket]; curr != null; curr = curr.nextInBucket) {
            if (curr.key.equals(key)) {
                if (recordAccess) {
                    recordExistingAccess(curr);    
                }                
                return curr.value;
            }
        }
        
        return null;
    }

    public V put(K key, V value) {
        final int bucket = getBucketIdx(key);

        // first iterate through the bucket to see if an entry already exists
        // for this key.  If so, update both its value and the key's place in the access order
        CacheEntry<K,V> prev = null;
        CacheEntry<K,V> curr = buckets[bucket];
        while (curr != null) {
            if (curr.key.equals(key)) {
                recordExistingAccess(curr);
                
                final V oldValue = curr.value;
                curr.value = value; 
                return oldValue;
            }
            prev = curr;
            curr = curr.nextInBucket;
        }

        // since we didn't return out of the above loop,
        // we know that we need to create a new entry and add it this bucket,
        // and the variable 'prev' points to the end of the bucket's linked list
        // if the bucket is not empty
        final CacheEntry<K,V> entry = new CacheEntry<K,V>(key, value);
        if (prev != null) {
            prev.nextInBucket = entry;
        } else { // the bucket is empty 
            buckets[bucket] = entry;
        }
        
        recordNewAccess(entry);

        return null;
    }
    
    // helper method for inserting a new entry into the doubly-linked list of access order
    private void recordNewAccess(final CacheEntry<K,V> entry) {
        // add the new entry to the end
        if (numEntries == 0) {
            firstAccessed = entry;
            lastAccessed = entry;
        } else {
            entry.prevAccessed = lastAccessed;
            lastAccessed.nextAccessed = entry;
            lastAccessed = entry;
        }
        
        // update the total number of entries, kicking out the
        // least recently used if we are already full
        if (numEntries < maxEntries) {
            ++numEntries;
        } else {
            assert numEntries == maxEntries;
            
            removeFromBucket(firstAccessed.key);
         
            firstAccessed = firstAccessed.nextAccessed;
            firstAccessed.prevAccessed = null;
        }
    }
    
    // helper method that only removes the entry for the input key from its bucket
    private void removeFromBucket(final K key) {
        final int bucket = getBucketIdx(key);
        
        CacheEntry<K,V> prev = null;
        CacheEntry<K,V> curr = buckets[bucket];
        while (curr != null) {
            if (curr.key.equals(key)) {
                if (prev != null) {
                    prev.nextInBucket = curr.nextInBucket;
                } else {
                    buckets[bucket] = curr.nextInBucket;
                }
                return;
            }
            prev = curr;
            curr = curr.nextInBucket;
        }
        assert false : "Could not find entry to remove from its bucket";
    }
    
    // helper method to make the input entry the most recently accessed one 
    private void recordExistingAccess(final CacheEntry<K,V> entry) {
        if (entry == lastAccessed) {
            return; // already in the desired state 
        }
        
        // remove entry from its current position
        if (entry.prevAccessed != null) {
            entry.prevAccessed.nextAccessed = entry.nextAccessed;
        }
        if (entry.nextAccessed != null) {
            entry.nextAccessed.prevAccessed = entry.prevAccessed;
        }
        if (entry == firstAccessed) {
            firstAccessed = entry.nextAccessed;
        }
        
        // add entry to the end
        entry.prevAccessed = lastAccessed;
        entry.nextAccessed = null;
        lastAccessed.nextAccessed = entry;
        lastAccessed = entry;
        
    }

    private static class CacheEntry<K, V> {
        final K key;
        V value;
        CacheEntry<K, V> nextInBucket = null;
        CacheEntry<K, V> nextAccessed = null;
        CacheEntry<K, V> prevAccessed = null;
        
        CacheEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    // helper method for debugging
    public String dumpState() {
        final StringBuilder sb = new StringBuilder();
        sb.append(numEntries).append(" entries in ").append(numBuckets).append(" buckets:\n\n");
        for (int i = 0; i < numBuckets; i++) {
            sb.append("Bucket ").append(i).append(": ");
            for (CacheEntry<K,V> curr = buckets[i]; curr != null; curr = curr.nextInBucket) {
                sb.append(String.format("(%s -> %s), ", curr.key, curr.value));
            }
            sb.append("\n");
        }
        sb.append("\nAccess Order:\n");
        int i = 0;
        for (CacheEntry<K,V> curr = firstAccessed; curr != null; curr = curr.nextAccessed, i++) {
            sb.append(String.format("%4d: (%s -> %s)\n", i, curr.key, curr.value));
        }
        return sb.toString();
    }
}
