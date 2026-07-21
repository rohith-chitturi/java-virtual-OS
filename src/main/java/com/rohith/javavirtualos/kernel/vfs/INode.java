package com.rohith.javavirtualos.kernel.vfs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class INode {
    private static long nextId = 1;
    
    private final long id;
    private final FileType type;
    private int ownerUid;
    private Set<Permission> permissions;
    
    private Instant createdAt;
    private Instant modifiedAt;
    private Instant accessedAt;
    
    private final List<DataBlock> blocks;
    
    public INode(FileType type, int ownerUid, Set<Permission> permissions) {
        this.id = nextId++;
        this.type = type;
        this.ownerUid = ownerUid;
        this.permissions = EnumSet.copyOf(permissions);
        this.createdAt = Instant.now();
        this.modifiedAt = this.createdAt;
        this.accessedAt = this.createdAt;
        this.blocks = new ArrayList<>();
    }

    public long getId() { return id; }
    public FileType getType() { return type; }
    
    public int getOwnerUid() { return ownerUid; }
    public void setOwnerUid(int ownerUid) { this.ownerUid = ownerUid; }
    
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = EnumSet.copyOf(permissions); }
    
    public Instant getCreatedAt() { return createdAt; }
    public Instant getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(Instant modifiedAt) { this.modifiedAt = modifiedAt; }
    public Instant getAccessedAt() { return accessedAt; }
    public void setAccessedAt(Instant accessedAt) { this.accessedAt = accessedAt; }
    
    public List<DataBlock> getBlocks() { return blocks; }
    
    public int getSize() {
        return blocks.stream().mapToInt(DataBlock::getSize).sum();
    }
}
