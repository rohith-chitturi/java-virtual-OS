package com.rohith.javavirtualos.shell.stream;

import java.io.InputStream;

/**
 * Standard console input stream.
 */
public class ConsoleInput extends VirtualInput {

    private final InputStream in;

    public ConsoleInput(InputStream in) {
        super("STDIN");
        this.in = in;
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public void close() {
        // We typically do not close System.in
    }
}
