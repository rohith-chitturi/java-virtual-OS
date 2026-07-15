package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;
import java.util.List;

public class HistoryCommand implements Command {
    
    private final List<String> history;

    public HistoryCommand(List<String> history) {
        this.history = history;
    }

    @Override
    public boolean execute(String[] args, ShellContext context) {
        int index = 1;
        for (String entry : history) {
            context.getOut().printf("%5d  %s%n", index++, entry);
        }
        return true;
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
