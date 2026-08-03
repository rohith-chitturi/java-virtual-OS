package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class MvCommand implements Command {

    private final FileSystemService fsService;

    public MvCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 2) {
            return CommandResult.failure("mv: missing file operand(s)");
        }
        
        String src = args[0];
        String dest = args[1];
        
        CommandResult catResult = fsService.catFile(src, context);
        if (!catResult.isSuccess()) {
            return CommandResult.failure("mv: cannot move '" + src + "': " + catResult.getMessage());
        }
        
        CommandResult writeResult = fsService.writeFile(dest, catResult.getMessage(), context);
        if (!writeResult.isSuccess()) {
            return CommandResult.failure("mv: failed to move '" + src + "' to '" + dest + "'");
        }
        
        return fsService.removeFile(src, context);
    }

    @Override
    public String getName() {
        return "mv";
    }

    @Override
    public String getDescription() {
        return "Move (rename) files";
    }
}
