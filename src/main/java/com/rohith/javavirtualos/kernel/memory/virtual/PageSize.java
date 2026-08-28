package com.rohith.javavirtualos.kernel.memory.virtual;

public enum PageSize {
    STANDARD(4096),
    HUGE(2 * 1024 * 1024);
    
    private final long bytes;
    
    PageSize(long bytes) {
        this.bytes = bytes;
    }
    
    public long getBytes() {
        return bytes;
    }
}
