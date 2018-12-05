package com.ryanwporter.salestax;

import java.util.concurrent.ThreadLocalRandom;

/*
 * Contains all of the constants, subject to constraints from the problem statement:
 * 
 *  "While there are billions of addresses, we've identified that 95% of lookups 
 *   come from a relatively small number of addresses (less than 100K addresses). 
 *   ...
 *   the cache can only be large enough to store approximately 50K addresses."
 *  
 * Also contains a generator of random street addresses, for use in a simulation.
 * The details of the distribution used here are arbitrarily
 * chosen, subject to the above constraints.  Specifically, the distribution is uniform
 * over the "common" addresses (i.e., the ones in the 95%) and also uniform over the "uncommon" ones.
 * 
 */
public class SalesTaxConstants {

    public static final long 
         NUM_UNCOMMON_ADDRESSES =  5 * 1000L * 1000L * 1000L
        ,NUM_COMMON_ADDRESSES   = 90 * 1000L
        ;
        
    public static final double FRAC_COMMON_ADDRESSES = 0.95;
    
    public static final int MAX_CACHE_ENTRIES = 50 * 1000;
    
    public static StreetAddress generateRandomAddress() {
        final long idx;
        if (ThreadLocalRandom.current().nextDouble() < FRAC_COMMON_ADDRESSES) {
            idx = ThreadLocalRandom.current().nextLong(NUM_COMMON_ADDRESSES);
        } else {
            idx = ThreadLocalRandom.current().nextLong(NUM_UNCOMMON_ADDRESSES) + NUM_COMMON_ADDRESSES;
        }
        return new StreetAddress(idx);
    }
}
