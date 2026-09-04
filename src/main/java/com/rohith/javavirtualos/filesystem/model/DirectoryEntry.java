package com.rohith.javavirtualos.filesystem.model;

/**
 * Maps a canonical name to an Inode within a DirectoryNode.
 * Essential for supporting hard links (N:1 relationships).
 */
public class DirectoryEntry {
    private final String name;
    private final Inode inode;

    public DirectoryEntry(String name, Inode inode) {
        this.name = name;
        this.inode = inode;
    }

    public String getName() {
        return name;
    }

    public Inode getInode() {
        return inode;
    }
}
