package com.rohith.javavirtualos.services;

import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;

/**
 * Service interface for all file system operations exposed to the Shell.
 */
public interface FileSystemService {
    CommandResult listDirectory(String path, ShellContext context);
    CommandResult makeDirectory(String path, ShellContext context);
    CommandResult removeDirectory(String path, ShellContext context);
    CommandResult removeFile(String path, ShellContext context);
    CommandResult changeDirectory(String path, ShellContext context);
    CommandResult touchFile(String path, ShellContext context);
    CommandResult printTree(String path, ShellContext context);
    CommandResult catFile(String path, ShellContext context);
    CommandResult writeFile(String path, String content, ShellContext context);
    CommandResult appendFile(String path, String content, ShellContext context);
}
