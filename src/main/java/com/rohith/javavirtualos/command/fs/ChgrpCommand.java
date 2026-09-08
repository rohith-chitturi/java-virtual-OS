package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class ChgrpCommand implements Command {
    private final FileSystemService fsService;

    public ChgrpCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public String getName() {
        return "chgrp";
    }

    @Override
    public String getDescription() {
        return "Change file group ownership.";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length != 2) {
            return CommandResult.failure("Usage: chgrp <group> <file>");
        }
        return fsService.chgrp(args[1], args[0], context);
    }
}
