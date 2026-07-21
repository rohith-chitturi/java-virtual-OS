package com.rohith.javavirtualos.kernel.vfs;

public class FileSystemStatistics {
    private long totalFiles = 0;
    private long totalDirectories = 0;
    private long openFiles = 0;
    private long reads = 0;
    private long writes = 0;
    private long deletes = 0;

    public void recordFileCreated() { totalFiles++; }
    public void recordDirectoryCreated() { totalDirectories++; }
    public void recordFileOpened() { openFiles++; }
    public void recordFileClosed() { openFiles--; }
    public void recordRead() { reads++; }
    public void recordWrite() { writes++; }
    public void recordDelete() { deletes++; }

    public long getTotalFiles() { return totalFiles; }
    public long getTotalDirectories() { return totalDirectories; }
    public long getOpenFiles() { return openFiles; }
    public long getReads() { return reads; }
    public long getWrites() { return writes; }
    public long getDeletes() { return deletes; }
}
