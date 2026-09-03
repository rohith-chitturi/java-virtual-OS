package com.rohith.javavirtualos.filesystem.model;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A DirectoryNode that can dynamically generate its children on demand.
 * It combines static children (added via addChild) with dynamic children.
 */
public class VirtualDirectoryNode extends DirectoryNode {

    private final Supplier<Collection<Inode>> dynamicChildrenSupplier;

    public VirtualDirectoryNode(String name, String owner, DirectoryNode parent, Supplier<Collection<Inode>> dynamicChildrenSupplier) {
        super(name, owner, parent);
        this.dynamicChildrenSupplier = dynamicChildrenSupplier;
    }

    private Map<String, Inode> getMergedChildren() {
        Map<String, Inode> merged = new HashMap<>();
        // Add static children
        for (Inode child : super.getChildren()) {
            merged.put(child.getName(), child);
        }
        // Add dynamic children
        if (dynamicChildrenSupplier != null) {
            Collection<Inode> dynamicChildren = dynamicChildrenSupplier.get();
            if (dynamicChildren != null) {
                for (Inode child : dynamicChildren) {
                    child.setParent(this);
                    merged.put(child.getName(), child);
                }
            }
        }
        return merged;
    }

    @Override
    public long calculateSize() {
        long total = 0;
        for (Inode child : getMergedChildren().values()) {
            total += child.calculateSize();
        }
        return total;
    }

    @Override
    public Inode getChild(String name) {
        return getMergedChildren().get(name);
    }

    @Override
    public Collection<Inode> getChildren() {
        return getMergedChildren().values();
    }

    @Override
    public boolean hasChild(String name) {
        return getMergedChildren().containsKey(name);
    }
}
