package com.rohith.javavirtualos.shell.stream;

import java.io.PrintStream;

/**
 * Abstraction for an output stream destination.
 */
public abstract class VirtualOutput extends VirtualStream {

    protected VirtualOutput(String streamName) {
        super(streamName);
    }

    /**
     * @return the underlying Java PrintStream.
     */
    public abstract PrintStream getPrintStream();

}
