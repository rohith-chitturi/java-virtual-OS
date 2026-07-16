package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.UserManager;
import com.rohith.javavirtualos.shell.ShellContext;

public class UseraddCommand implements Command {

    private final UserManager userManager;

    public UseraddCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (!"root".equals(context.getCurrentUser().getUsername())) {
            return CommandResult.failure("useradd: Permission denied. Must be root.");
        }

        if (args.length < 1) {
            return CommandResult.failure("Usage: useradd <username> [password]");
        }

        String username = args[0];
        String password = args.length > 1 ? args[1] : "";

        boolean success = userManager.addUser(username, password);
        if (success) {
            return CommandResult.success("User " + username + " added successfully.");
        } else {
            return CommandResult.failure("useradd: user '" + username + "' already exists");
        }
    }

    @Override
    public String getName() {
        return "useradd";
    }

    @Override
    public String getDescription() {
        return "Add a new user (requires root)";
    }
}
