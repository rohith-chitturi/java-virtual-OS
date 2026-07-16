package com.rohith.javavirtualos.filesystem.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a directory composite in the virtual file system.
 */
public class DirectoryNode extends Inode {

    private final Map<String, Inode> children;

    public DirectoryNode(String name, String owner, DirectoryNode parent) {
        super(name, owner, parent);
        this.children = new HashMap<>();
    }

    @Override
    public FileType getType() {
        return FileType.DIRECTORY;
    }

    @Override
    public long calculateSize() {
        long total = 0;
        for (Inode child : children.values()) {
            total += child.calculateSize();
        }
        return total;
    }

    public void addChild(Inode child) {
        children.put(child.getName(), child);
        child.setParent(this);
        metadata.updateModified();
    }

    public void removeChild(String name) {
        children.remove(name);
        metadata.updateModified();
    }

    public Inode getChild(String name) {
        return children.get(name);
    }

    public Collection<Inode> getChildren() {
        return children.values();
    }
    
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }
}
