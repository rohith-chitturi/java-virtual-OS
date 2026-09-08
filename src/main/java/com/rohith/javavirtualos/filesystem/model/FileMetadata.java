package com.rohith.javavirtualos.filesystem.model;

/**
 * Metadata associated with any filesystem node.
 */
public class FileMetadata {
    
    private long size;
    private final long createdAt;
    private long modifiedAt;
    private String owner;
    private String group;
    private FileMode mode;

    public FileMetadata(String owner) {
        this.size = 0;
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = this.createdAt;
        this.owner = owner;
        this.group = "users"; // default group
        // Default modes can be customized by creation context (umask), but we provide a baseline
        this.mode = new FileMode((short) 0644); 
    }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; updateModified(); }

    public long getCreatedAt() { return createdAt; }

    public long getModifiedAt() { return modifiedAt; }
    public void updateModified() { this.modifiedAt = System.currentTimeMillis(); }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; updateModified(); }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; updateModified(); }

    public FileMode getMode() { return mode; }
    public void setMode(FileMode mode) { this.mode = mode; updateModified(); }
}
