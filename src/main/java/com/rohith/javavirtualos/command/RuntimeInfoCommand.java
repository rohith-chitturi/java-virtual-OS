package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.runtime.RuntimeStatistics;
import com.rohith.javavirtualos.shell.ShellContext;

public class RuntimeInfoCommand implements Command {

    private final RuntimeStatistics stats;

    public RuntimeInfoCommand(RuntimeStatistics stats) {
        this.stats = stats;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Virtual OS Runtime Statistics ---\n");
        sb.append("Executables Loaded: ").append(stats.getExecutablesLoaded()).append("\n");
        sb.append("Instructions Executed: ").append(stats.getInstructionsExecuted()).append("\n");
        sb.append("System Calls Invoked: ").append(stats.getSystemCallsInvoked()).append("\n");
        sb.append("Runtime Faults: ").append(stats.getRuntimeFaults());
        
        return CommandResult.success(sb.toString());
    }

    @Override
    public String getName() {
        return "runtimeinfo";
    }

    @Override
    public String getDescription() {
        return "Displays statistics about the executable runtime.";
    }
}
