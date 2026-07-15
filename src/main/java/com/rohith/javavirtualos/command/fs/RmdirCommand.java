package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class RmdirCommand implements Command {

    private final FileSystemService fsService;

    public RmdirCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length == 0) {
            return CommandResult.failure("rmdir: missing operand");
        }
        return fsService.removeDirectory(args[0], context);
    }

    @Override
    public String getName() {
        return "rmdir";
    }

    @Override
    public String getDescription() {
        return "Remove an empty directory";
    }
}
