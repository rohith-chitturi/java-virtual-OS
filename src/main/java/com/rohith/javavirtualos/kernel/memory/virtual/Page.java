package com.rohith.javavirtualos.kernel.memory.virtual;

public class Page {
    private final int pid;
    private final long pageNumber;
    
    public Page(int pid, long pageNumber) {
        this.pid = pid;
        this.pageNumber = pageNumber;
    }

    public int getPid() { return pid; }
    public long getPageNumber() { return pageNumber; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return pid == page.pid && pageNumber == page.pageNumber;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(pid);
        result = 31 * result + Long.hashCode(pageNumber);
        return result;
    }
    
    @Override
    public String toString() {
        return "Page(PID=" + pid + ", VPN=" + pageNumber + ")";
    }
}
