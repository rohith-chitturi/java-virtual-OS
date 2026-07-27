package com.rohith.javavirtualos.kernel.process.descriptor;

public interface Descriptor {
    int getFd();
    void setFd(int fd);
    void close();
    boolean isOpen();
}
