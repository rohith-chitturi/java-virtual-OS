package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.services.DefaultProcessService;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class PstreeCommand implements Command {

    private final ProcessService processService;

    public PstreeCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (!(processService instanceof DefaultProcessService)) {
            return CommandResult.failure("Process service does not support pstree.");
        }
        
        List<ProcessControlBlock> allProcs = ((DefaultProcessService) processService).getManager().listProcesses();
        StringBuilder sb = new StringBuilder();
        
        for (ProcessControlBlock pcb : allProcs) {
            boolean hasParent = allProcs.stream().anyMatch(p -> p.getPid() == pcb.getParentPid());
            if (!hasParent) {
                printTree(pcb, allProcs, sb, 0, "");
            }
        }
        
        return CommandResult.success(sb.toString().trim());
    }
    
    private void printTree(ProcessControlBlock node, List<ProcessControlBlock> allProcs, StringBuilder sb, int depth, String prefix) {
        sb.append(prefix);
        if (depth > 0) {
            sb.append("├── ");
        }
        sb.append(node.getCommandName()).append(" (PID ").append(node.getPid()).append(")\n");
        
        String newPrefix = prefix + (depth > 0 ? "│   " : "");
        for (int childPid : node.getChildrenPids()) {
            ProcessControlBlock child = allProcs.stream().filter(p -> p.getPid() == childPid).findFirst().orElse(null);
            if (child != null) {
                printTree(child, allProcs, sb, depth + 1, newPrefix);
            }
        }
    }

    @Override
    public String getName() {
        return "pstree";
    }

    @Override
    public String getDescription() {
        return "Show process tree";
    }
}
