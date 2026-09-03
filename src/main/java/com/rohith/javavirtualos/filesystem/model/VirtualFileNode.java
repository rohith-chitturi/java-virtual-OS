package com.rohith.javavirtualos.filesystem.model;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import java.util.function.Supplier;

/**
 * A FileNode whose content is generated dynamically upon read.
 * Used for /proc and /sys filesystems.
 */
public class VirtualFileNode extends FileNode {

    private final Supplier<String> contentGenerator;

    public VirtualFileNode(String name, String owner, DirectoryNode parent, Supplier<String> contentGenerator) {
        super(name, owner, parent);
        this.contentGenerator = contentGenerator;
    }

    @Override
    public long calculateSize() {
        return getContent().length();
    }

    @Override
    public String getContent() {
        return contentGenerator.get();
    }

    @Override
    public void setContent(String content) {
        throw new UnsupportedOperationException("Virtual files are read-only.");
    }
    
    @Override
    public void appendContent(String additional) {
        throw new UnsupportedOperationException("Virtual files are read-only.");
    }
}
