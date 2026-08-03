package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

public class AffinityCommand implements Command {
    private final ProcessService processService;

    public AffinityCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public String getName() { return "affinity"; }

    @Override
    public String getDescription() { return "Display or modify CPU affinity for a process. Usage: affinity <pid> [mask]"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 1) return CommandResult.failure("Usage: affinity <pid> [mask]");

        try {
            int pid = Integer.parseInt(args[0]);
            ProcessControlBlock pcb = processService.getManager().findByPID(pid);
            if (pcb == null) return CommandResult.failure("Process " + pid + " not found.");

            if (args.length == 1) {
                return CommandResult.success("Process " + pid + " affinity mask: " + Long.toBinaryString(pcb.getSchedulingInfo().getCpuAffinityMask()));
            } else {
                long mask = Long.parseLong(args[1], 2);
                pcb.getSchedulingInfo().setCpuAffinityMask(mask);
                return CommandResult.success("Set affinity mask for process " + pid + " to " + Long.toBinaryString(mask));
            }
        } catch (NumberFormatException e) {
            return CommandResult.failure("Invalid PID or mask format.");
        }
    }
}
