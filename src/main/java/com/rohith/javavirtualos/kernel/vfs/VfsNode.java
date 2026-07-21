package com.rohith.javavirtualos.kernel.vfs;

public abstract class VfsNode {
    protected String name;
    protected final INode inode;
    protected VfsDirectory parent;

    public VfsNode(String name, INode inode) {
        this.name = name;
        this.inode = inode;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public INode getINode() { return inode; }
    
    public VfsDirectory getParent() { return parent; }
    public void setParent(VfsDirectory parent) { this.parent = parent; }
    
    public abstract boolean isDirectory();
}
