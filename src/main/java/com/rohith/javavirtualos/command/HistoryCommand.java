package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;
import java.util.List;

public class HistoryCommand implements Command {
    
    private final List<String> history;

    public HistoryCommand(List<String> history) {
        this.history = history;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (String entry : history) {
            sb.append(String.format("%5d  %s%n", index++, entry));
        }
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public String getDescription() {
        return "Print command history";
    }
}
