package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class CatCommand implements Command {

    private final FileSystemService fsService;

    public CatCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length == 0) {
            return CommandResult.failure("cat: missing file operand");
        }
        return fsService.catFile(args[0], context);
    }

    @Override
    public String getName() {
        return "cat";
    }

    @Override
    public String getDescription() {
        return "Concatenate and print file contents";
    }
}
