package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class WhoamiCommand implements Command {

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (context.getCurrentUser() != null) {
            return CommandResult.success(context.getCurrentUser().getUsername());
        }
        return CommandResult.failure("whoami: no current user");
    }

    @Override
    public String getName() {
        return "whoami";
    }

    @Override
    public String getDescription() {
        return "Print the current user";
    }
}
