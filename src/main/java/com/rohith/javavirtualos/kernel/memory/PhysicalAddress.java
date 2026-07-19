package com.rohith.javavirtualos.kernel.memory;

public class PhysicalAddress implements Comparable<PhysicalAddress> {
    private final long address;

    public PhysicalAddress(long address) {
        this.address = address;
    }

    public long getAddress() { return address; }

    public PhysicalAddress plus(MemorySize size) {
        return new PhysicalAddress(this.address + size.toBytes());
    }

    @Override
    public int compareTo(PhysicalAddress o) {
        return Long.compare(this.address, o.address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhysicalAddress that = (PhysicalAddress) o;
        return address == that.address;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(address);
    }

    @Override
    public String toString() {
        return "0x" + Long.toHexString(address).toUpperCase();
    }
}
