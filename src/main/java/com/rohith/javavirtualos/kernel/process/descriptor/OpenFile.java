package com.rohith.javavirtualos.kernel.process.descriptor;

import com.rohith.javavirtualos.filesystem.model.Inode;

public class OpenFile implements Descriptor {
    private int fd;
    private final Inode file;
    private boolean open;
    private int cursor;

    public OpenFile(Inode file) {
        this.file = file;
        this.open = true;
        this.cursor = 0;
    }

    public int getCursor() { return cursor; }
    public void setCursor(int cursor) { this.cursor = cursor; }

    @Override
    public int getFd() { return fd; }

    @Override
    public void setFd(int fd) { this.fd = fd; }

    @Override
    public void close() { this.open = false; }

    @Override
    public boolean isOpen() { return open; }

    public Inode getFile() { return file; }
}
