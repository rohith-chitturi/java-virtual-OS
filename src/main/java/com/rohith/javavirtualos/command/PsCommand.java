package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

public class PsCommand implements Command {

    private final ProcessService processService;

    public PsCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return processService.listProcesses(context);
    }

    @Override
    public String getName() {
        return "ps";
    }

    @Override
    public String getDescription() {
        return "List active processes";
    }
}
