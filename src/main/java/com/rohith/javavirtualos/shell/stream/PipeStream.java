package com.rohith.javavirtualos.shell.stream;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * A VirtualStream that connects an output to an input, allowing IPC between commands (e.g. command1 | command2).
 */
public class PipeStream {

    private final VirtualInput inputEnd;
    private final VirtualOutput outputEnd;

    public PipeStream(String name) throws IOException {
        PipedInputStream pin = new PipedInputStream();
        PipedOutputStream pout = new PipedOutputStream(pin);

        this.inputEnd = new VirtualInput(name + "_in") {
            @Override
            public InputStream getInputStream() {
                return pin;
            }
            @Override
            public void close() {
                try { pin.close(); } catch (Exception ignored) {}
            }
        };

        this.outputEnd = new VirtualOutput(name + "_out") {
            @Override
            public java.io.PrintStream getPrintStream() {
                return new java.io.PrintStream(pout);
            }
            @Override
            public void close() {
                try { pout.close(); } catch (Exception ignored) {}
            }
        };
    }

    public VirtualInput getInputEnd() {
        return inputEnd;
    }

    public VirtualOutput getOutputEnd() {
        return outputEnd;
    }
}
