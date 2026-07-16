package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.Arrays;

public class AppendCommand implements Command {

    private final FileSystemService fsService;

    public AppendCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 2) {
            return CommandResult.failure("append: missing operand (Usage: append <file> <content>)");
        }
        String path = args[0];
        String content = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        return fsService.appendFile(path, content, context);
    }

    @Override
    public String getName() {
        return "append";
    }

    @Override
    public String getDescription() {
        return "Append text to an existing file";
    }
}
