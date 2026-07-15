package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class MkdirCommand implements Command {

    private final FileSystemService fsService;

    public MkdirCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length == 0) {
            return CommandResult.failure("mkdir: missing operand");
        }
        return fsService.makeDirectory(args[0], context);
    }

    @Override
    public String getName() {
        return "mkdir";
    }

    @Override
    public String getDescription() {
        return "Create a directory";
    }
}
