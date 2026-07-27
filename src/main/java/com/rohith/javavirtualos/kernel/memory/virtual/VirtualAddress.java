package com.rohith.javavirtualos.kernel.memory.virtual;

public class VirtualAddress implements Comparable<VirtualAddress> {
    private final long address;

    public VirtualAddress(long address) {
        this.address = address;
    }

    public long getAddress() { return address; }

    public long getPageNumber(long pageSizeBytes) {
        return address / pageSizeBytes;
    }

    public long getOffset(long pageSizeBytes) {
        return address % pageSizeBytes;
    }

    @Override
    public int compareTo(VirtualAddress o) {
        return Long.compare(this.address, o.address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualAddress that = (VirtualAddress) o;
        return address == that.address;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(address);
    }

    @Override
    public String toString() {
        return String.format("0x%08X", address);
    }
}
