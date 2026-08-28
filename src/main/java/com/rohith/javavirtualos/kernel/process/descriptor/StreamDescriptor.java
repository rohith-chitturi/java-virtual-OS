package com.rohith.javavirtualos.kernel.process.descriptor;

import java.io.InputStream;
import java.io.PrintStream;

public class StreamDescriptor implements Descriptor {
    private int fd;
    private final InputStream in;
    private final PrintStream out;

    public StreamDescriptor(InputStream in) {
        this.in = in;
        this.out = null;
    }

    public StreamDescriptor(PrintStream out) {
        this.in = null;
        this.out = out;
    }

    @Override
    public int getFd() { return fd; }

    @Override
    public void setFd(int fd) { this.fd = fd; }

    @Override
    public void close() {
        // Typically don't close System.out/in wrapped streams here unless it's a file
    }

    @Override
    public boolean isOpen() { return true; }

    public InputStream getInputStream() { return in; }
    public PrintStream getPrintStream() { return out; }
}
