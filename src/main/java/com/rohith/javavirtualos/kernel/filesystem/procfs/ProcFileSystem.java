package com.rohith.javavirtualos.kernel.filesystem.procfs;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.DirectoryEntry;
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

public class ProcFileSystem {

    public static void mount(FileSystemManager fsManager, ProcessManager processManager, MemoryManager memoryManager, SystemContext context, KernelLogBuffer logBuffer) {
        try {
            DirectoryNode root = fsManager.getRoot();
            
            // Define /proc as a VirtualDirectoryNode with dynamic PID children
            VirtualDirectoryNode procDir = new VirtualDirectoryNode("root", () -> {
                List<DirectoryEntry> dynamicProcs = new ArrayList<>();
                for (ProcessControlBlock pcb : processManager.listProcesses()) {
                    String pidName = String.valueOf(pcb.getPid());
                    VirtualDirectoryNode pidDir = createPidDirectory(pcb, processManager);
                    dynamicProcs.add(new DirectoryEntry(pidName, pidDir));
                }
                return dynamicProcs;
            });
            
            root.addChild("proc", procDir);

            // Add static virtual files
            procDir.addChild("cpuinfo", new VirtualFileNode("root", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("processor : 0\n");
                sb.append("state     : RUNNING\n"); 
                sb.append("scheduler : CFS\n");
                return sb.toString();
            }));

            procDir.addChild("meminfo", new VirtualFileNode("root", () -> {
                long total = memoryManager.getStatistics().getTotalMemory().toBytes();
                long used = memoryManager.getStatistics().getUsedMemory().toBytes();
                long free = total - used;
                return String.format("MemTotal: %8d kB\nMemFree:  %8d kB\n", total / 1024, free / 1024);
            }));

            procDir.addChild("uptime", new VirtualFileNode("root", () -> {
                long uptime = System.currentTimeMillis() - context.getBootTime();
                return String.format("%.2f 0.00\n", uptime / 1000.0);
            }));

            procDir.addChild("mounts", new VirtualFileNode("root", () -> {
                return "rootfs / rootfs rw 0 0\nproc /proc proc rw 0 0\nsysfs /sys sysfs rw 0 0\n";
            }));

            procDir.addChild("kmsg", new VirtualFileNode("root", () -> {
                return logBuffer.getContents();
            }));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static VirtualDirectoryNode createPidDirectory(ProcessControlBlock pcb, ProcessManager processManager) {
        VirtualDirectoryNode pidDir = new VirtualDirectoryNode(pcb.getOwner().getUsername(), null);
        
        pidDir.addChild("status", new VirtualFileNode(pcb.getOwner().getUsername(), () -> {
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
        
        pidDir.addChild("stat", new VirtualFileNode(pcb.getOwner().getUsername(), () -> {
            return String.format("%d (%s) %s %d %d", pcb.getPid(), pcb.getCommandName(), 
                pcb.getState().name().substring(0, 1), pcb.getParentPid(), pcb.getPgid());
        }));
        
        pidDir.addChild("maps", new VirtualFileNode(pcb.getOwner().getUsername(), () -> {
            StringBuilder sb = new StringBuilder();
            if (pcb.getVmas() != null) {
                for (var vma : pcb.getVmas()) {
                    sb.append(vma.toString()).append("\n");
                }
            }
            return sb.toString();
        }));

        VirtualDirectoryNode fdDir = new VirtualDirectoryNode(pcb.getOwner().getUsername(), () -> {
            List<DirectoryEntry> fds = new ArrayList<>();
            Map<Integer, Descriptor> table = pcb.getFileDescriptorTable().getAll();
            for (Map.Entry<Integer, Descriptor> entry : table.entrySet()) {
                if (entry.getValue() != null) {
                    int fdNum = entry.getKey();
                    Descriptor desc = entry.getValue();
                    String fdName = String.valueOf(fdNum);
                    VirtualFileNode fdNode = new VirtualFileNode(pcb.getOwner().getUsername(), () -> {
                        if (desc instanceof OpenFile) {
                            OpenFile of = (OpenFile) desc;
                            // getAbsolutePath is gone from Inode. We'll use a placeholder for now.
                            // In a real implementation we'd need to reconstruct the path via PathResolver.
                            return "fd=" + fdNum + "\npath=[deleted]\nmode=READ_WRITE\ntype=FILE\n";
                        } else if (desc instanceof StreamDescriptor) {
                            return "fd=" + fdNum + "\npath=pipe/stream\ntype=STREAM\n";
                        }
                        return "fd=" + fdNum + "\n";
                    });
                    fds.add(new DirectoryEntry(fdName, fdNode));
                }
            }
            return fds;
        });
        pidDir.addChild("fd", fdDir);

        return pidDir;
    }
}
