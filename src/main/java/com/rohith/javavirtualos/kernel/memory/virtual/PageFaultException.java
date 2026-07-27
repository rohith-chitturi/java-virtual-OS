package com.rohith.javavirtualos.kernel.memory.virtual;

public class PageFaultException extends RuntimeException {
    private final VirtualAddress address;
    private final Page page;

    public PageFaultException(VirtualAddress address, Page page) {
        super("Page fault at " + address);
        this.address = address;
        this.page = page;
    }

    public VirtualAddress getAddress() { return address; }
    public Page getPage() { return page; }
}
