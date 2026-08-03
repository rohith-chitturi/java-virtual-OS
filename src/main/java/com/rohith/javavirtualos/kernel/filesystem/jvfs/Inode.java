package com.rohith.javavirtualos.kernel.filesystem.jvfs;

import com.rohith.javavirtualos.kernel.security.PermissionBits;
import java.nio.ByteBuffer;

/**
 * Represents an Index Node (Inode) in the JVFS.
 * Stores metadata about a file or directory and pointers to its data blocks.
 */
public class Inode {
    public static final int INODE_SIZE = 128; // Fixed size per inode
    public static final int DIRECT_BLOCKS = 12;

    private int inodeNumber;
    private int fileType; // 0 = Unknown, 1 = File, 2 = Directory, 3 = Symlink, 4 = Device
    private int fileSize;
    private int uid;
    private int gid;
    private int permissions; // Representation of PermissionBits
    private long creationTime;
    private long modificationTime;
    
    // Block pointers
    private final int[] directBlocks = new int[DIRECT_BLOCKS];
    private int singleIndirectBlock;
    
    public Inode(int inodeNumber, int fileType, int uid, int gid, int permissions) {
        this.inodeNumber = inodeNumber;
        this.fileType = fileType;
        this.uid = uid;
        this.gid = gid;
        this.permissions = permissions;
        this.fileSize = 0;
        long now = System.currentTimeMillis();
        this.creationTime = now;
        this.modificationTime = now;
        
        for (int i = 0; i < DIRECT_BLOCKS; i++) {
            directBlocks[i] = -1; // -1 means unallocated
        }
        singleIndirectBlock = -1;
    }
    
    // For deserialization
    public Inode() {
    }
    
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(INODE_SIZE);
        buffer.putInt(inodeNumber);
        buffer.putInt(fileType);
        buffer.putInt(fileSize);
        buffer.putInt(uid);
        buffer.putInt(gid);
        buffer.putInt(permissions);
        buffer.putLong(creationTime);
        buffer.putLong(modificationTime);
        
        for (int i = 0; i < DIRECT_BLOCKS; i++) {
            buffer.putInt(directBlocks[i]);
        }
        buffer.putInt(singleIndirectBlock);
        
        return buffer.array();
    }
    
    public static Inode deserialize(byte[] data, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, INODE_SIZE);
        Inode inode = new Inode();
        inode.inodeNumber = buffer.getInt();
        inode.fileType = buffer.getInt();
        inode.fileSize = buffer.getInt();
        inode.uid = buffer.getInt();
        inode.gid = buffer.getInt();
        inode.permissions = buffer.getInt();
        inode.creationTime = buffer.getLong();
        inode.modificationTime = buffer.getLong();
        
        for (int i = 0; i < DIRECT_BLOCKS; i++) {
            inode.directBlocks[i] = buffer.getInt();
        }
        inode.singleIndirectBlock = buffer.getInt();
        return inode;
    }

    public int getInodeNumber() { return inodeNumber; }
    public int getFileType() { return fileType; }
    public int getFileSize() { return fileSize; }
    public void setFileSize(int size) { this.fileSize = size; this.modificationTime = System.currentTimeMillis(); }
    public int getUid() { return uid; }
    public int getGid() { return gid; }
    public int getPermissions() { return permissions; }
    public void setPermissions(int perms) { this.permissions = perms; }
    
    public int[] getDirectBlocks() { return directBlocks; }
    public int getSingleIndirectBlock() { return singleIndirectBlock; }
    public void setSingleIndirectBlock(int block) { this.singleIndirectBlock = block; }
}
