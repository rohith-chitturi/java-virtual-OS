package com.rohith.javavirtualos.shell.stream;

import java.io.InputStream;

/**
 * Abstraction for an input stream source.
 */
public abstract class VirtualInput extends VirtualStream {

    protected VirtualInput(String streamName) {
        super(streamName);
    }

    /**
     * @return the underlying Java InputStream.
     */
    public abstract InputStream getInputStream();

}
