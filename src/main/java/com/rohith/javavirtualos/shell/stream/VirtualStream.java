package com.rohith.javavirtualos.shell.stream;

/**
 * Base abstraction for all stream types in the virtual OS.
 * Files, Sockets, Pipes, Devices, and Console will inherit from this.
 */
public abstract class VirtualStream {
    
    private final String streamName;

    protected VirtualStream(String streamName) {
        this.streamName = streamName;
    }

    public String getStreamName() {
        return streamName;
    }
    
    /**
     * Closes the stream, releasing any associated resources.
     */
    public abstract void close();
}
