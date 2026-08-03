package com.rohith.javavirtualos.shell.stream;

import java.io.PrintStream;

/**
 * Standard console output stream.
 */
public class ConsoleOutput extends VirtualOutput {

    private final PrintStream out;

    public ConsoleOutput(PrintStream out) {
        super("STDOUT");
        this.out = out;
    }

    @Override
    public PrintStream getPrintStream() {
        return out;
    }

    @Override
    public void close() {
        // We typically do not close System.out
        out.flush();
    }
}
