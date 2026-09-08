package com.rohith.javavirtualos.command.fs;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class ChmodCommand implements Command {
    private final FileSystemService fsService;

    public ChmodCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public String getName() {
        return "chmod";
    }

    @Override
    public String getDescription() {
        return "Change file mode bits.";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length != 2) {
            return CommandResult.failure("Usage: chmod <octal_mode> <file>");
        }
        try {
            short mode = Short.parseShort(args[0], 8);
            return fsService.chmod(args[1], mode, context);
        } catch (NumberFormatException e) {
            return CommandResult.failure("Invalid mode format. Please use octal (e.g. 755).");
        }
    }
}
