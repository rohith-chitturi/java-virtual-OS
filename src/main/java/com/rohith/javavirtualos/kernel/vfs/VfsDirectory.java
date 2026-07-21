package com.rohith.javavirtualos.kernel.vfs;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class VfsDirectory extends VfsNode {
    private final Map<String, VfsNode> children;

    public VfsDirectory(String name, INode inode) {
        super(name, inode);
        if (inode.getType() != FileType.DIRECTORY) {
            throw new IllegalArgumentException("INode must be of type DIRECTORY");
        }
        this.children = new LinkedHashMap<>();
    }

    @Override
    public boolean isDirectory() { return true; }
    
    public void addChild(VfsNode child) {
        child.setParent(this);
        children.put(child.getName(), child);
    }
    
    public void removeChild(String name) {
        VfsNode child = children.remove(name);
        if (child != null) {
            child.setParent(null);
        }
    }
    
    public Optional<VfsNode> getChild(String name) {
        return Optional.ofNullable(children.get(name));
    }
    
    public Collection<VfsNode> getChildren() {
        return children.values();
    }
}
