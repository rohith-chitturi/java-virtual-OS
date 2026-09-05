package com.rohith.javavirtualos.filesystem.model;

public class SymlinkNode extends Inode {
    
    private final String targetPath;

    public SymlinkNode(String owner, String targetPath) {
        super(owner);
        this.targetPath = targetPath;
    }

    @Override
    public FileType getType() {
        return FileType.LINK;
    }

    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public long calculateSize() {
        return targetPath.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
    }
}
