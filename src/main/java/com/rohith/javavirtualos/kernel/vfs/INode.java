package com.rohith.javavirtualos.kernel.vfs;

import java.time.Instant;
import java.util.ArrayList;
import com.rohith.javavirtualos.kernel.security.PermissionBits;
import java.util.List;

public class INode {
    private static long nextId = 1;
    
    private final long id;
    private final FileType type;
    private int ownerUid;
    private int ownerGid;
    private PermissionBits permissions;
    
    private Instant createdAt;
    private Instant modifiedAt;
    private Instant accessedAt;
    
    private final List<DataBlock> blocks;
    
    public INode(FileType type, int ownerUid, int ownerGid, PermissionBits permissions) {
        this.id = nextId++;
        this.type = type;
        this.ownerUid = ownerUid;
        this.ownerGid = ownerGid;
        this.permissions = permissions;
        this.createdAt = Instant.now();
        this.modifiedAt = this.createdAt;
        this.accessedAt = this.createdAt;
        this.blocks = new ArrayList<>();
    }

    public long getId() { return id; }
    public FileType getType() { return type; }
    
    public int getOwnerUid() { return ownerUid; }
    public void setOwnerUid(int ownerUid) { this.ownerUid = ownerUid; }
    
    public int getOwnerGid() { return ownerGid; }
    public void setOwnerGid(int ownerGid) { this.ownerGid = ownerGid; }
    
    public PermissionBits getPermissions() { return permissions; }
    public void setPermissions(PermissionBits permissions) { this.permissions = permissions; }
    
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
