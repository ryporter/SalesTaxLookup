package com.ryanwporter.salestax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/*
 * Simple, incomplete unit test for MyLruCache
 * 
 */
public class MyLruCacheTest {

    // the cache we are testing
    private MyLruCache<Integer,Integer> cache;
    
    // contains the contents that we expect to exist in the cache
    private final Map<Integer,Integer> expected = new HashMap<>();
    
    private void checkCache() {
        // System.out.println(cache.dumpState());
        
        // check that all of the entries we expect exist
        for (Map.Entry<Integer, Integer> e : expected.entrySet()) {
            Integer actualValue = cache.getWithoutRecordAccess(e.getKey());
            if (actualValue == null) {
                fail("Missing expected value for key: " + e.getKey());
            } else {
                assertEquals("Wrong value for key: " + e.getKey(), e.getValue(), actualValue);
            }
        }
        
        //  check that entries that we do not expect don't exist
        for (int i = 0; i < 20; i++) {
            if (!expected.containsKey(i)) {
                final Integer v = cache.getWithoutRecordAccess(i);
                if (v != null) {
                    fail("Expected no value for key " + i + ", but saw " + v);
                }
            }
        }
        
        assertEquals("Cache is wrong size", expected.size(), cache.size());
        assertEquals("Inconsistent isEmpty", expected.isEmpty(), cache.isEmpty());
    }
    
    private void checkPut(int key, int value) {
        cache.put(key, value);
        expected.put(key, value);
        checkCache();
    }
    
    private void checkGet(int key) {
        cache.get(key);
        checkCache();
    }
    
    @Test public void testCache() throws Exception {
        cache = new MyLruCache<>(4);
        
        checkCache();
        checkPut(3, 5);
        checkPut(2, 6);
        checkPut(3, 7);
        
        checkPut(1, 8);
        checkPut(4, 9);
        
        // adding the next new key should kick out Key 2
        expected.remove(2);
        checkPut(5, 2);
        
        checkGet(3);
        
        expected.remove(1);
        checkPut(6, 2);
    }
    
    // exercise some edge cases in the implementation by using a cache of size 1
    @Test public void testSizeOne() throws Exception {
        cache = new MyLruCache<>(1);
        
        checkCache();
        checkPut(3, 5);
        
        expected.remove(3);
        checkPut(2, 6);
    }
}
