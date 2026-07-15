package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class RmCommand implements Command {

    private final FileSystemService fsService;

    public RmCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length == 0) {
            return CommandResult.failure("rm: missing operand");
        }
        return fsService.removeFile(args[0], context);
    }

    @Override
    public String getName() {
        return "rm";
    }

    @Override
    public String getDescription() {
        return "Remove a file";
    }
}
