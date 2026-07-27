package com.rohith.javavirtualos.kernel.process.descriptor;

import com.rohith.javavirtualos.kernel.vfs.VfsFile;

public class OpenFile implements Descriptor {
    private int fd;
    private final VfsFile file;
    private boolean open;

    public OpenFile(VfsFile file) {
        this.file = file;
        this.open = true;
    }

    @Override
    public int getFd() { return fd; }

    @Override
    public void setFd(int fd) { this.fd = fd; }

    @Override
    public void close() { this.open = false; }

    @Override
    public boolean isOpen() { return open; }

    public VfsFile getFile() { return file; }
}
