package com.rohith.javavirtualos.kernel.filesystem.procfs;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.VirtualDirectoryNode;
import com.rohith.javavirtualos.filesystem.model.VirtualFileNode;
import com.rohith.javavirtualos.kernel.device.DeviceManager;
import com.rohith.javavirtualos.kernel.memory.MemoryManager;

public class SysFileSystem {

    public static void mount(FileSystemManager fsManager, DeviceManager deviceManager, MemoryManager memoryManager) {
        try {
            DirectoryNode root = fsManager.getRoot();
            
            VirtualDirectoryNode sysDir = new VirtualDirectoryNode("root", null);
            root.addChild("sys", sysDir);

            // /sys/cpu
            VirtualDirectoryNode cpuDir = new VirtualDirectoryNode("root", null);
            sysDir.addChild("cpu", cpuDir);
            cpuDir.addChild("online", new VirtualFileNode("root", () -> "0-1\n")); // Simulated 2 CPUs

            // /sys/memory
            VirtualDirectoryNode memDir = new VirtualDirectoryNode("root", null);
            sysDir.addChild("memory", memDir);
            memDir.addChild("block_size", new VirtualFileNode("root", () -> "4096\n"));

            // /sys/devices
            VirtualDirectoryNode devDir = new VirtualDirectoryNode("root", null);
            sysDir.addChild("devices", devDir);
            devDir.addChild("system", new VirtualFileNode("root", () -> "system devices\n"));
            
            // /sys/block
            VirtualDirectoryNode blockDir = new VirtualDirectoryNode("root", null);
            sysDir.addChild("block", blockDir);
            blockDir.addChild("vdisk0", new VirtualFileNode("root", () -> "size: 1048576\n")); // Mock stats

            // /sys/network
            VirtualDirectoryNode netDir = new VirtualDirectoryNode("root", null);
            sysDir.addChild("network", netDir);
            
            // /sys/kernel
            VirtualDirectoryNode kernelDir = new VirtualDirectoryNode("root", null);
            sysDir.addChild("kernel", kernelDir);
            kernelDir.addChild("version", new VirtualFileNode("root", () -> "JavaOS v0.1.0-alpha\n"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
