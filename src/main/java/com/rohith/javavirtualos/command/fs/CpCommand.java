package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class CpCommand implements Command {

    private final FileSystemService fsService;

    public CpCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 2) {
            return CommandResult.failure("cp: missing file operand(s)");
        }
        
        String src = args[0];
        String dest = args[1];
        
        CommandResult catResult = fsService.catFile(src, context);
        if (!catResult.isSuccess()) {
            return CommandResult.failure("cp: cannot copy '" + src + "': " + catResult.getMessage());
        }
        
        return fsService.writeFile(dest, catResult.getMessage(), context);
    }

    @Override
    public String getName() {
        return "cp";
    }

    @Override
    public String getDescription() {
        return "Copy files and directories";
    }
}
