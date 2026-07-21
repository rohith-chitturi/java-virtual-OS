package com.rohith.javavirtualos.kernel.vfs;

public class OpenFile {
    private final INode inode;
    private int offset;
    private int referenceCount;

    public OpenFile(INode inode) {
        this.inode = inode;
        this.offset = 0;
        this.referenceCount = 1;
    }

    public INode getINode() { return inode; }
    
    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }
    public void advance(int amount) { this.offset += amount; }
    
    public int getReferenceCount() { return referenceCount; }
    public void incrementReference() { this.referenceCount++; }
    public void decrementReference() { this.referenceCount--; }
}
