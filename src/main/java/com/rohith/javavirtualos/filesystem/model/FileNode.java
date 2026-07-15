package com.rohith.javavirtualos.filesystem.model;

/**
 * Represents a standard file in the virtual file system.
 */
public class FileNode extends Inode {

    private String content;

    public FileNode(String name, String owner, DirectoryNode parent) {
        super(name, owner, parent);
        this.content = "";
    }

    @Override
    public FileType getType() {
        return FileType.FILE;
    }

    @Override
    public long calculateSize() {
        return content.length();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        metadata.setSize(content.length());
        metadata.updateModified();
    }
    
    public void appendContent(String additional) {
        this.content += additional;
        metadata.setSize(this.content.length());
        metadata.updateModified();
    }
}
