package com.rohith.javavirtualos.kernel.process.descriptor;

import com.rohith.javavirtualos.kernel.network.socket.VirtualSocket;

public class OpenSocket implements Descriptor {
    private int fd;
    private final VirtualSocket socket;
    private boolean open;

    public OpenSocket(VirtualSocket socket) {
        this.socket = socket;
        this.open = true;
    }

    @Override
    public int getFd() { return fd; }

    @Override
    public void setFd(int fd) { this.fd = fd; }

    @Override
    public void close() { 
        this.open = false; 
        socket.close();
    }

    @Override
    public boolean isOpen() { return open; }

    public VirtualSocket getSocket() { return socket; }
}
