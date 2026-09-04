package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.memory.virtual.VirtualMemoryArea;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

public class VmMapCommand implements Command {
    private final ProcessService processService;

    public VmMapCommand(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public String getName() { return "vmmap"; }

    @Override
    public String getDescription() { return "Display a process's Virtual Memory Areas (VMAs)"; }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 2) return CommandResult.failure("Usage: vmmap <PID>");
        
        try {
            int pid = Integer.parseInt(args[1]);
            ProcessControlBlock pcb = processService.getManager().findByPID(pid);
            
            if (pcb == null) {
                return CommandResult.failure("Process " + pid + " not found.");
            }
            
            List<VirtualMemoryArea> vmas = pcb.getVmas();
            if (vmas.isEmpty()) {
                return CommandResult.success("No VMAs found for PID " + pid);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("VMAs for PID %d (%s):\n", pid, pcb.getCommandName()));
            sb.append(String.format("%-18s %-18s %-6s %-10s %-12s %s\n", "START", "END", "PERM", "TYPE", "PAGESIZE", "BACKING FILE"));
            sb.append("--------------------------------------------------------------------------------\n");
            
            for (VirtualMemoryArea vma : vmas) {
                String backing = vma.getBackingFile() != null ? "inode:" + vma.getBackingFile().getInodeId() : "[none]";
                sb.append(String.format("0x%-16x 0x%-16x %-6s %-10s %-12s %s\n", 
                    vma.getStartAddress().getAddress(), 
                    vma.getEndAddress().getAddress(), 
                    vma.getPermissions(), 
                    vma.getType(), 
                    vma.getPageSize(), 
                    backing));
            }
            
            return CommandResult.success(sb.toString().trim());
        } catch (NumberFormatException e) {
            return CommandResult.failure("Invalid PID: " + args[1]);
        }
    }
}
