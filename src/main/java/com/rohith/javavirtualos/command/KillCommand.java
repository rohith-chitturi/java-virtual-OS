package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

public class KillCommand implements Command {

    private final ProcessService processService;

    public KillCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length == 0) {
            return CommandResult.failure("kill: missing pid");
        }
        try {
            int pid = Integer.parseInt(args[0]);
            return processService.killProcess(pid, context);
        } catch (NumberFormatException e) {
            return CommandResult.failure("kill: invalid pid format");
        }
    }

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public String getDescription() {
        return "Terminate a process by PID";
    }
}
