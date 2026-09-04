package com.rohith.javavirtualos.filesystem.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a directory composite in the virtual file system.
 */
public class DirectoryNode extends Inode {

    private final Map<String, DirectoryEntry> children;

    public DirectoryNode(String owner) {
        super(owner);
        this.children = new HashMap<>();
    }

    @Override
    public FileType getType() {
        return FileType.DIRECTORY;
    }

    @Override
    public long calculateSize() {
        long total = 0;
        for (DirectoryEntry entry : children.values()) {
            total += entry.getInode().calculateSize();
        }
        return total;
    }

    public void addChild(String name, Inode child) {
        DirectoryEntry entry = new DirectoryEntry(name, child);
        children.put(name, entry);
        metadata.updateModified();
    }

    public void removeChild(String name) {
        DirectoryEntry entry = children.remove(name);
        if (entry != null) {
            metadata.updateModified();
        }
    }

    public Inode getChild(String name) {
        DirectoryEntry entry = children.get(name);
        return entry != null ? entry.getInode() : null;
    }
    
    public DirectoryEntry getChildEntry(String name) {
        return children.get(name);
    }

    public Collection<DirectoryEntry> getEntries() {
        return children.values();
    }

    public Collection<Inode> getChildren() {
        return children.values().stream().map(DirectoryEntry::getInode).toList();
    }
    
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }
}
