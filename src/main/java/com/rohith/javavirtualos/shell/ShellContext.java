package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.kernel.User;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Represents the localized terminal state.
 */
public class ShellContext {
    
    private final SystemContext systemContext;
    private User currentUser;
    private String currentDirectory;
    private final PrintStream out;
    private final InputStream in;
    private final java.util.Map<String, String> aliases;

    public ShellContext(SystemContext systemContext, User initialUser, PrintStream out, InputStream in) {
        this.systemContext = systemContext;
        this.currentUser = initialUser;
        this.currentDirectory = "/";
        this.out = out;
        this.in = in;
        this.aliases = new java.util.HashMap<>();
    }

    public java.util.Map<String, String> getAliases() {
        return aliases;
    }

    public SystemContext getSystemContext() {
        return systemContext;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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
