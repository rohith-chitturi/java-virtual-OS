package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.User;
import com.rohith.javavirtualos.kernel.UserManager;
import com.rohith.javavirtualos.shell.ShellContext;
import java.util.Scanner;

public class SuCommand implements Command {

    private final UserManager userManager;

    public SuCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        String targetUsername = args.length > 0 ? args[0] : "root";
        User targetUser = userManager.getUser(targetUsername);

        if (targetUser == null) {
            return CommandResult.failure("su: user " + targetUsername + " does not exist");
        }

        // Simplistic password prompt simulation
        if (!"root".equals(context.getCurrentUser().getUsername())) {
            context.getOut().print("Password: ");
            Scanner scanner = new Scanner(context.getIn());
            String password = "";
            if (scanner.hasNextLine()) {
                password = scanner.nextLine().trim();
            }
            if (!targetUser.authenticate(password)) {
                return CommandResult.failure("su: Authentication failure");
            }
        }

        context.setCurrentUser(targetUser);
        return CommandResult.success("");
    }

    @Override
    public String getName() {
        return "su";
    }

    @Override
    public String getDescription() {
        return "Switch user";
    }
}
