package com.rohith.javavirtualos.command.device;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.services.FileSystemService;

public class MountCommand implements Command {
    private final FileSystemService fsService;

    public MountCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public String getName() {
        return "mount";
    }

    @Override
    public String getDescription() {
        return "Displays mounted devices";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        context.getOut().println("Mounted points in /dev:");
        try {
            return new com.rohith.javavirtualos.command.fs.LsCommand(fsService).execute(new String[]{"/dev"}, context);
        } catch (Exception e) {
            context.getOut().println("Error displaying mounts: " + e.getMessage());
            return CommandResult.failure("Failed to list mounts");
        }
    }
}
