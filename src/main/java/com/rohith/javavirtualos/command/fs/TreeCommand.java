package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class TreeCommand implements Command {

    private final FileSystemService fsService;

    public TreeCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        String target = args.length == 0 ? null : args[0];
        return fsService.printTree(target, context);
    }

    @Override
    public String getName() {
        return "tree";
    }

    @Override
    public String getDescription() {
        return "List directory contents in a tree-like format";
    }
}
