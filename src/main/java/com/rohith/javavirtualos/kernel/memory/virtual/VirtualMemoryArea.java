package com.rohith.javavirtualos.kernel.memory.virtual;

import com.rohith.javavirtualos.filesystem.model.FileNode;

public class VirtualMemoryArea {
    private final VirtualAddress startAddress;
    private final VirtualAddress endAddress;
    private final String permissions; // e.g., "rwx"
    private final VMAType type;
    private final FileNode backingFile;
    private final long offset;
    private final PageSize pageSize;

    public VirtualMemoryArea(VirtualAddress startAddress, VirtualAddress endAddress, String permissions, VMAType type, FileNode backingFile, long offset, PageSize pageSize) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.permissions = permissions;
        this.type = type;
        this.backingFile = backingFile;
        this.offset = offset;
        this.pageSize = pageSize;
    }

    public VirtualAddress getStartAddress() { return startAddress; }
    public VirtualAddress getEndAddress() { return endAddress; }
    public String getPermissions() { return permissions; }
    public VMAType getType() { return type; }
    public FileNode getBackingFile() { return backingFile; }
    public long getOffset() { return offset; }
    public PageSize getPageSize() { return pageSize; }

    public boolean contains(VirtualAddress address) {
        long addr = address.getAddress();
        return addr >= startAddress.getAddress() && addr < endAddress.getAddress();
    }
}
