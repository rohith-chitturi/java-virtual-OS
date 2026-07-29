package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.core.CPU;
import com.rohith.javavirtualos.kernel.core.Processor;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class CpuInfoCommand implements Command {
    private final ProcessService processService;

    public CpuInfoCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public String getName() { return "cpuinfo"; }

    @Override
    public String getDescription() { return "Displays CPU architecture and per-core statistics"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        KernelDispatcher dispatcher = processService.getDispatcher();
        if (dispatcher == null) return CommandResult.failure("CPU scheduler not initialized.");

        Processor processor = dispatcher.getProcessor();
        List<CPU> cores = processor.getCores();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Architecture: %s (%d cores)\n", processor.getClass().getSimpleName(), processor.getCoreCount()));
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-6s %-12s %-12s %-15s\n", "CORE", "STATE", "LOAD(Q)", "CURRENT PROCESS"));

        for (int i = 0; i < cores.size(); i++) {
            CPU cpu = cores.get(i);
            int qSize = dispatcher.getCoreSchedulers().get(i).getReadyQueue().size();
            ProcessControlBlock curr = cpu.getCurrentProcess();
            String pName = curr != null ? curr.getCommandName() + " (PID " + curr.getPid() + ")" : "IDLE";
            sb.append(String.format("%-6d %-12s %-12d %-15s\n", 
                    cpu.getCoreId(), cpu.getState(), qSize, pName));
        }

        return CommandResult.success(sb.toString().trim());
    }
}
