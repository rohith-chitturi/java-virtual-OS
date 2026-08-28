package com.rohith.javavirtualos.kernel.memory.virtual;

public class CowFaultException extends RuntimeException {
    private final VirtualAddress virtualAddress;
    private final Page page;

    public CowFaultException(VirtualAddress virtualAddress, Page page) {
        super("Copy-on-Write fault at virtual address: " + virtualAddress.getAddress());
        this.virtualAddress = virtualAddress;
        this.page = page;
    }

    public VirtualAddress getVirtualAddress() { return virtualAddress; }
    public Page getPage() { return page; }
}
