package com.rohith.javavirtualos.filesystem.model;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import java.util.function.Supplier;

/**
 * A FileNode whose content is generated dynamically upon read.
 * Used for /proc and /sys filesystems.
 */
public class VirtualFileNode extends FileNode {

    private final Supplier<String> contentGenerator;

    public VirtualFileNode(String owner, Supplier<String> contentGenerator) {
        super(owner);
        this.contentGenerator = contentGenerator;
    }

    @Override
    public long calculateSize() {
        return generateContentBytes().length;
    }

    public byte[] generateContentBytes() {
        return contentGenerator.get().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
