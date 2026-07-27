package com.rohith.javavirtualos.kernel.process.pcb;

public class ResourceInfo {
    private long memoryUsage;
    private long cpuTime;
    private int openFiles;

    public ResourceInfo(long initialMemory) {
        this.memoryUsage = initialMemory;
        this.cpuTime = 0;
        this.openFiles = 0;
    }

    public long getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(long memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public long getCpuTime() { return cpuTime; }
    public void setCpuTime(long cpuTime) { this.cpuTime = cpuTime; }
    
    public int getOpenFiles() { return openFiles; }
    public void setOpenFiles(int openFiles) { this.openFiles = openFiles; }
}
