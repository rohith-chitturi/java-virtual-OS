package com.rohith.javavirtualos.filesystem.model;

/**
 * Represents a standard file in the virtual file system.
 */
public class FileNode extends Inode {

    public FileNode(String owner) {
        super(owner);
    }

    public FileNode(long inodeId, String owner) {
        super(inodeId, owner);
    }

    @Override
    public FileType getType() {
        return FileType.FILE;
    }

    @Override
    public long calculateSize() {
        return metadata.getSize();
    }
}
