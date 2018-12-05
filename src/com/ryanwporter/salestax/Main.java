package com.ryanwporter.salestax;

/*
 * Simplistic simulator to exercise the cache.  Simulates a bunch of calls
 * and prints out the cache hit rate.
 */
public class Main {
    
    public static void main(String[] args) {
        final SalesTaxCache cache = new SalesTaxCache();
        
        for (long i = 0; i < 1e7; i++) {
            final StreetAddress addr = SalesTaxConstants.generateRandomAddress();
            cache.fastTaxLookup(addr);
        }
        
        System.out.println(cache);
    }
}
