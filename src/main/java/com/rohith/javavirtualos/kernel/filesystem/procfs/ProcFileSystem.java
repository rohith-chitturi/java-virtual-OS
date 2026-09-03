package com.rohith.javavirtualos.kernel.filesystem.procfs;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.filesystem.model.VirtualDirectoryNode;
import com.rohith.javavirtualos.filesystem.model.VirtualFileNode;
import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.kernel.memory.MemoryManager;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;
import com.rohith.javavirtualos.kernel.events.KernelLogBuffer;
import com.rohith.javavirtualos.kernel.process.descriptor.Descriptor;
import com.rohith.javavirtualos.kernel.process.descriptor.OpenFile;
import com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProcFileSystem {

    public static void mount(FileSystemManager fsManager, ProcessManager processManager, MemoryManager memoryManager, SystemContext context, KernelLogBuffer logBuffer) {
        try {
            DirectoryNode root = fsManager.getRoot();
            
            // Define /proc as a VirtualDirectoryNode with dynamic PID children
            VirtualDirectoryNode procDir = new VirtualDirectoryNode("proc", "root", root, () -> {
                List<Inode> dynamicProcs = new ArrayList<>();
                for (ProcessControlBlock pcb : processManager.listProcesses()) {
                    VirtualDirectoryNode pidDir = createPidDirectory(pcb, processManager);
                    dynamicProcs.add(pidDir);
                }
                return dynamicProcs;
            });
            
            root.addChild(procDir);

            // Add static virtual files
            procDir.addChild(new VirtualFileNode("cpuinfo", "root", procDir, () -> {
                StringBuilder sb = new StringBuilder();
                // Simple representation for now. A real OS might have more complex CPU info.
                sb.append("processor : 0\n");
                sb.append("state     : RUNNING\n"); // Placeholder, ideally from Scheduler/CPU
                sb.append("scheduler : CFS\n");
                return sb.toString();
            }));

            procDir.addChild(new VirtualFileNode("meminfo", "root", procDir, () -> {
                long total = memoryManager.getStatistics().getTotalMemory().toBytes();
                long used = memoryManager.getStatistics().getUsedMemory().toBytes();
                long free = total - used;
                return String.format("MemTotal: %8d kB\nMemFree:  %8d kB\n", total / 1024, free / 1024);
            }));

            procDir.addChild(new VirtualFileNode("uptime", "root", procDir, () -> {
                long uptime = System.currentTimeMillis() - context.getBootTime();
                return String.format("%.2f 0.00\n", uptime / 1000.0);
            }));

            procDir.addChild(new VirtualFileNode("mounts", "root", procDir, () -> {
                // Static representation for now
                return "rootfs / rootfs rw 0 0\nproc /proc proc rw 0 0\nsysfs /sys sysfs rw 0 0\n";
            }));

            procDir.addChild(new VirtualFileNode("kmsg", "root", procDir, () -> {
                return logBuffer.getContents();
            }));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static VirtualDirectoryNode createPidDirectory(ProcessControlBlock pcb, ProcessManager processManager) {
        // Parent will be set when returned list is evaluated, so we can pass null for now
        VirtualDirectoryNode pidDir = new VirtualDirectoryNode(String.valueOf(pcb.getPid()), pcb.getOwner().getUsername(), null, null);
        
        pidDir.addChild(new VirtualFileNode("status", pcb.getOwner().getUsername(), pidDir, () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Name:       ").append(pcb.getCommandName()).append("\n");
            sb.append("PID:        ").append(pcb.getPid()).append("\n");
            sb.append("TGID:       ").append(pcb.getTgid()).append("\n");
            sb.append("PPID:       ").append(pcb.getParentPid()).append("\n");
            sb.append("PGID:       ").append(pcb.getPgid()).append("\n");
            sb.append("State:      ").append(pcb.getState().name()).append("\n");
            sb.append("OwnerUid:   ").append(pcb.getOwnerUid()).append("\n");
            return sb.toString();
        }));
        
        pidDir.addChild(new VirtualFileNode("stat", pcb.getOwner().getUsername(), pidDir, () -> {
            return String.format("%d (%s) %s %d %d", pcb.getPid(), pcb.getCommandName(), 
                pcb.getState().name().substring(0, 1), pcb.getParentPid(), pcb.getPgid());
        }));
        
        pidDir.addChild(new VirtualFileNode("maps", pcb.getOwner().getUsername(), pidDir, () -> {
            StringBuilder sb = new StringBuilder();
            if (pcb.getVmas() != null) {
                for (var vma : pcb.getVmas()) {
                    sb.append(vma.toString()).append("\n");
                }
            }
            return sb.toString();
        }));

        VirtualDirectoryNode fdDir = new VirtualDirectoryNode("fd", pcb.getOwner().getUsername(), pidDir, () -> {
            List<Inode> fds = new ArrayList<>();
            Map<Integer, Descriptor> table = pcb.getFileDescriptorTable().getAll();
            for (Map.Entry<Integer, Descriptor> entry : table.entrySet()) {
                if (entry.getValue() != null) {
                    int fdNum = entry.getKey();
                    Descriptor desc = entry.getValue();
                    fds.add(new VirtualFileNode(String.valueOf(fdNum), pcb.getOwner().getUsername(), null, () -> {
                        if (desc instanceof OpenFile) {
                            OpenFile of = (OpenFile) desc;
                            return "fd=" + fdNum + "\npath=" + of.getFile().getAbsolutePath() + "\nmode=READ_WRITE\ntype=FILE\n";
                        } else if (desc instanceof StreamDescriptor) {
                            return "fd=" + fdNum + "\npath=pipe/stream\ntype=STREAM\n";
                        }
                        return "fd=" + fdNum + "\n";
                    }));
                }
            }
            return fds;
        });
        pidDir.addChild(fdDir);

        return pidDir;
    }
}
