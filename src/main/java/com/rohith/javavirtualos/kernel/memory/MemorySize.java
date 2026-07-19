package com.rohith.javavirtualos.kernel.memory;

public class MemorySize implements Comparable<MemorySize> {
    private final long bytes;

    private MemorySize(long bytes) {
        this.bytes = bytes;
    }

    public static MemorySize ofBytes(long bytes) { return new MemorySize(bytes); }
    public static MemorySize ofKB(long kb) { return new MemorySize(kb * 1024); }
    public static MemorySize ofMB(long mb) { return new MemorySize(mb * 1024 * 1024); }
    public static MemorySize ofGB(long gb) { return new MemorySize(gb * 1024 * 1024 * 1024); }

    public long toBytes() { return bytes; }
    public long toKB() { return bytes / 1024; }
    public long toMB() { return bytes / (1024 * 1024); }

    public MemorySize add(MemorySize other) {
        return new MemorySize(this.bytes + other.bytes);
    }

    public MemorySize subtract(MemorySize other) {
        return new MemorySize(this.bytes - other.bytes);
    }

    @Override
    public int compareTo(MemorySize o) {
        return Long.compare(this.bytes, o.bytes);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemorySize that = (MemorySize) o;
        return bytes == that.bytes;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(bytes);
    }

    @Override
    public String toString() {
        if (bytes >= 1024 * 1024 * 1024 && bytes % (1024 * 1024 * 1024) == 0) return toMB() / 1024 + " GB";
        if (bytes >= 1024 * 1024 && bytes % (1024 * 1024) == 0) return toMB() + " MB";
        if (bytes >= 1024 && bytes % 1024 == 0) return toKB() + " KB";
        return bytes + " B";
    }
}
