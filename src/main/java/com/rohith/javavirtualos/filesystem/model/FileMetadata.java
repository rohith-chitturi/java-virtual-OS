package com.rohith.javavirtualos.filesystem.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Metadata associated with any filesystem node.
 */
public class FileMetadata {
    
    private String name;
    private long size;
    private final long createdAt;
    private long modifiedAt;
    private String owner;
    private Set<Permission> permissions;
    private boolean hidden;
    private boolean readOnly;

    public FileMetadata(String name, String owner) {
        this.name = name;
        this.size = 0;
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = this.createdAt;
        this.owner = owner;
        this.permissions = EnumSet.of(Permission.READ, Permission.WRITE);
        this.hidden = name.startsWith(".");
        this.readOnly = false;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; this.hidden = name.startsWith("."); updateModified(); }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; updateModified(); }

    public long getCreatedAt() { return createdAt; }

    public long getModifiedAt() { return modifiedAt; }
    public void updateModified() { this.modifiedAt = System.currentTimeMillis(); }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; updateModified(); }

    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; updateModified(); }

    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; updateModified(); }

    public boolean isReadOnly() { return readOnly; }
    public void setReadOnly(boolean readOnly) { this.readOnly = readOnly; updateModified(); }
}
