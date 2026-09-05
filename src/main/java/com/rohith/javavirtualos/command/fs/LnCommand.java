package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.shell.stream.VirtualInput;
import com.rohith.javavirtualos.shell.stream.VirtualOutput;

public class LnCommand implements Command {
    private final FileSystemService fsService;

    public LnCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public String getName() {
        return "ln";
    }

    @Override
    public String getDescription() {
        return "Create a hard link to an existing file.";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length != 2) {
            return CommandResult.failure("Usage: ln <target> <link_name>");
        }
        return fsService.createHardLink(args[0], args[1], context);
    }
}
