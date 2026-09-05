package com.rohith.javavirtualos.kernel.process.descriptor;

import com.rohith.javavirtualos.filesystem.model.Inode;

public class OpenFile implements Descriptor {
    private int fd;
    private final Inode file;
    private final com.rohith.javavirtualos.filesystem.InodeLifecycleManager lifecycleManager;
    private boolean open;
    private int cursor;

    public OpenFile(Inode file, com.rohith.javavirtualos.filesystem.InodeLifecycleManager lifecycleManager) {
        this.file = file;
        this.lifecycleManager = lifecycleManager;
        this.open = true;
        this.cursor = 0;
    }

    public OpenFile(Inode file) {
        this(file, null);
    }

    public int getCursor() { return cursor; }
    public void setCursor(int cursor) { this.cursor = cursor; }

    @Override
    public int getFd() { return fd; }

    @Override
    public void setFd(int fd) { this.fd = fd; }

    @Override
    public void close() { 
        if (this.open) {
            this.open = false; 
            if (lifecycleManager != null) {
                lifecycleManager.decrementOpenReference(this.file);
            }
        }
    }

    @Override
    public boolean isOpen() { return open; }

    public Inode getFile() { return file; }
}
