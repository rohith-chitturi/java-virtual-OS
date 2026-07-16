package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;

public class SleepCommand implements Command {

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        int seconds = 1; // default
        if (args.length > 0) {
            try {
                seconds = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return CommandResult.failure("sleep: invalid time interval");
            }
        }
        
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CommandResult.failure("sleep: interrupted");
        }
        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "sleep";
    }

    @Override
    public String getDescription() {
        return "Delay for a specified amount of time";
    }
}
