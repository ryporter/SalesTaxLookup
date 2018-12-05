package com.ryanwporter.salestax;


/*
 * Caches the results of sales tax computations, subject to a limit on the number of entries.
 * 
 * Internally, it uses a LRU cache, striking a compromise between simplicity and performance.
 * Improvements could be made by, for example, maintaining counts for each cache entry and
 * using them in the cache eviction decision. 
 * 
 * In order to determine the optimal caching strategy, we would need to know more about
 * the underlying distribution of street addresses.  However, for the one assumed in
 * SalesTaxConstants (namely, separately uniform over 90K common and 5B uncommon addresses),
 * we can compare the performance of LRU with the optimal strategy as follows:
 * 
 * An optimal, fully filled cache would contain 50K common addresses.
 * 
 * On given a call, there is a 95% chance that the input is one of the common addresses,
 * and otherwise we obviously have a miss.
 * 
 * That common address has a 50K / 90K chance of being in the cache.  
 * 
 * Thus, the overall hit rate is: 0.95 * 50K / 90K, which is approximately 53%.
 * 
 * Empirically, this solution has an approximately 49% hit rate. On a large project,
 * I would push this code and then re-check for the current performance bottlenecks.  
 * 
 * 
 * 
 * NB: Not Thread Safe
 */
public class SalesTaxCache {
    
    private final LruCache<StreetAddress, Double> cache = new LruCache<>(SalesTaxConstants.MAX_CACHE_ENTRIES);

    public SalesTaxCache() { }
    
    public double fastTaxLookup(final StreetAddress addr) {
        Double rate = cache.get(addr);
        if (rate == null) {
            rate = SalesTax.salesTaxLookup(addr);
            cache.put(addr, rate);
            
            ++numMisses;
        } else {
            ++numHits;
        }
        return rate;        
    }
    
    // state to track the performance of the cache
    private long numHits = 0;
    private long numMisses = 0;
    
    public double getHitRate() {
        final long numTotal = numHits + numMisses;
        if (numTotal == 0) {
            return 1.0; // could just as well return 0.0 here
        } else {
            return ((double) numHits) / numTotal;
        }
    }
    
    @Override public final String toString() {
        return String.format(
                "%d Hits, %d Misses: %.4f hit rate", 
                numHits, numMisses, getHitRate()
               );
    }
}
