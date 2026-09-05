package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.shell.stream.VirtualInput;
import com.rohith.javavirtualos.shell.stream.VirtualOutput;

public class ReadlinkCommand implements Command {
    private final FileSystemService fsService;

    public ReadlinkCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public String getName() {
        return "readlink";
    }

    @Override
    public String getDescription() {
        return "Print value of a symbolic link.";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length != 1) {
            return CommandResult.failure("Usage: readlink <path>");
        }
        return fsService.readlink(args[0], context);
    }
}
