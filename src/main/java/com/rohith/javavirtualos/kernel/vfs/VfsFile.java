package com.rohith.javavirtualos.kernel.vfs;

public class VfsFile extends VfsNode {
    public VfsFile(String name, INode inode) {
        super(name, inode);
        if (inode.getType() != FileType.FILE) {
            throw new IllegalArgumentException("INode must be of type FILE");
        }
    }

    @Override
    public boolean isDirectory() { return false; }
}
