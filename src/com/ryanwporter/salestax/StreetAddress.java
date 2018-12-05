package com.ryanwporter.salestax;

/*
 * Dummy class for a street address, where addresses are ridiculously, uniquely defined by an index.
 */
public class StreetAddress {
    public final long idx;
    
    public StreetAddress(final long idx) {
        this.idx = idx;
    }
    
    @Override public boolean equals(Object obj) {
        if (!(obj instanceof StreetAddress)) { return false; }
        final StreetAddress other = (StreetAddress) obj;
        return idx == other.idx;
    }
    
    @Override public int hashCode() {
        return Long.hashCode(idx);
    }
}
