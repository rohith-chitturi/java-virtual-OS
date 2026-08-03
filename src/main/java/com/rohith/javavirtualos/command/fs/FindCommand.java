package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class FindCommand implements Command {

    private final FileSystemService fsService;

    public FindCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        String path = null;
        String pattern = "";

        if (args.length == 0) {
            return CommandResult.failure("find: missing pattern");
        } else if (args.length == 1) {
            pattern = args[0];
        } else {
            path = args[0];
            pattern = args[1];
        }

        return fsService.findFile(path, pattern, context);
    }

    @Override
    public String getName() {
        return "find";
    }

    @Override
    public String getDescription() {
        return "Search for files in a directory hierarchy";
    }
}
