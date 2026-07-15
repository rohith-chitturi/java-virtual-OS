package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.kernel.SystemContext;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Represents the localized terminal state.
 */
public class ShellContext {
    
    private final SystemContext systemContext;
    private String currentDirectory;
    private final PrintStream out;
    private final InputStream in;

    public ShellContext(SystemContext systemContext, PrintStream out, InputStream in) {
        this.systemContext = systemContext;
        this.currentDirectory = "/";
        this.out = out;
        this.in = in;
    }

    public SystemContext getSystemContext() {
        return systemContext;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    
    public PrintStream getOut() {
        return out;
    }
    
    public InputStream getIn() {
        return in;
    }
}
