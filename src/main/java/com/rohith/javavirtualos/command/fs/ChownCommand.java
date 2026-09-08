package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class ChownCommand implements Command {
    private final FileSystemService fsService;

    public ChownCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public String getName() {
        return "chown";
    }

    @Override
    public String getDescription() {
        return "Change file owner.";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length != 2) {
            return CommandResult.failure("Usage: chown <owner> <file>");
        }
        return fsService.chown(args[1], args[0], context);
    }
}
