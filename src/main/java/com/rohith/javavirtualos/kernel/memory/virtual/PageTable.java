package com.rohith.javavirtualos.kernel.memory.virtual;

import java.util.HashMap;
import java.util.Map;

public class PageTable {
    private final int pid;
    private final Map<Long, PageTableEntry> entries;

    public PageTable(int pid) {
        this.pid = pid;
        this.entries = new HashMap<>();
    }

    public int getPid() { return pid; }

    public PageTableEntry getEntry(long pageNumber) {
        return entries.computeIfAbsent(pageNumber, k -> new PageTableEntry(new Page(pid, pageNumber)));
    }
    
    public Map<Long, PageTableEntry> getAllEntries() {
        return entries;
    }
}
