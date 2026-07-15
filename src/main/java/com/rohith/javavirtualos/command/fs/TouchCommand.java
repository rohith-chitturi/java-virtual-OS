package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class TouchCommand implements Command {

    private final FileSystemService fsService;

    public TouchCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length == 0) {
            return CommandResult.failure("touch: missing file operand");
        }
        return fsService.touchFile(args[0], context);
    }

    @Override
    public String getName() {
        return "touch";
    }

    @Override
    public String getDescription() {
        return "Create an empty file";
    }
}
