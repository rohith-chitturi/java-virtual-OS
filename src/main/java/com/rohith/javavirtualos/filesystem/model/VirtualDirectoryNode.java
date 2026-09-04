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

    private final Supplier<Collection<DirectoryEntry>> childrenSupplier;

    public VirtualDirectoryNode(String owner, Supplier<Collection<DirectoryEntry>> childrenSupplier) {
        super(owner);
        this.childrenSupplier = childrenSupplier;
    }

    private Map<String, DirectoryEntry> getMergedChildren() {
        Map<String, DirectoryEntry> merged = new HashMap<>();
        // Add static children
        for (DirectoryEntry entry : super.getEntries()) {
            merged.put(entry.getName(), entry);
        }
        // Add dynamic children
        if (childrenSupplier != null) {
            Collection<DirectoryEntry> dynamicChildren = childrenSupplier.get();
            if (dynamicChildren != null) {
                for (DirectoryEntry entry : dynamicChildren) {
                    merged.put(entry.getName(), entry);
                }
            }
        }
        return merged;
    }

    @Override
    public long calculateSize() {
        long total = 0;
        for (DirectoryEntry entry : getMergedChildren().values()) {
            total += entry.getInode().calculateSize();
        }
        return total;
    }

    @Override
    public Inode getChild(String name) {
        DirectoryEntry entry = getMergedChildren().get(name);
        return entry != null ? entry.getInode() : null;
    }
    
    @Override
    public DirectoryEntry getChildEntry(String name) {
        return getMergedChildren().get(name);
    }

    @Override
    public Collection<DirectoryEntry> getEntries() {
        return getMergedChildren().values();
    }

    @Override
    public Collection<Inode> getChildren() {
        return getMergedChildren().values().stream().map(DirectoryEntry::getInode).toList();
    }

    @Override
    public boolean hasChild(String name) {
        return getMergedChildren().containsKey(name);
    }
}
