package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class CdCommand implements Command {

    private final FileSystemService fsService;

    public CdCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        String target = args.length == 0 ? "~" : args[0];
        return fsService.changeDirectory(target, context);
    }

    @Override
    public String getName() {
        return "cd";
    }

    @Override
    public String getDescription() {
        return "Change directory";
    }
}
