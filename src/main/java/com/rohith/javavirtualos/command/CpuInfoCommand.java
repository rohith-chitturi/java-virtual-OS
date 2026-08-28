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
        sb.append(String.format("Processor: %s\n\n", processor.getClass().getSimpleName()));

        for (int i = 0; i < cores.size(); i++) {
            CPU cpu = cores.get(i);
            int qSize = dispatcher.getCoreSchedulers().get(i).getReadyQueue().size();
            ProcessControlBlock curr = cpu.getCurrentProcess();
            
            sb.append(String.format("Core %d\n", cpu.getCoreId()));
            if (curr != null) {
                sb.append(String.format("  Running PID %d (%s)\n", curr.getPid(), curr.getCommandName()));
                int simulatedLoad = 10 + (qSize * 15);
                if (simulatedLoad > 100) simulatedLoad = 100;
                sb.append(String.format("  Load %d%%\n\n", simulatedLoad));
            } else {
                sb.append("  Idle\n\n");
            }
        }

        return CommandResult.success(sb.toString().trim());
    }
}
