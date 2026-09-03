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
            
            VirtualDirectoryNode sysDir = new VirtualDirectoryNode("sys", "root", root, null);
            root.addChild(sysDir);

            // /sys/cpu
            VirtualDirectoryNode cpuDir = new VirtualDirectoryNode("cpu", "root", sysDir, null);
            sysDir.addChild(cpuDir);
            cpuDir.addChild(new VirtualFileNode("online", "root", cpuDir, () -> "0-1\n")); // Simulated 2 CPUs

            // /sys/memory
            VirtualDirectoryNode memDir = new VirtualDirectoryNode("memory", "root", sysDir, null);
            sysDir.addChild(memDir);
            memDir.addChild(new VirtualFileNode("block_size", "root", memDir, () -> "4096\n"));

            // /sys/devices
            VirtualDirectoryNode devDir = new VirtualDirectoryNode("devices", "root", sysDir, null);
            sysDir.addChild(devDir);
            devDir.addChild(new VirtualFileNode("system", "root", devDir, () -> "system devices\n"));
            
            // /sys/block
            VirtualDirectoryNode blockDir = new VirtualDirectoryNode("block", "root", sysDir, null);
            sysDir.addChild(blockDir);
            blockDir.addChild(new VirtualFileNode("vdisk0", "root", blockDir, () -> "size: 1048576\n")); // Mock stats

            // /sys/network
            VirtualDirectoryNode netDir = new VirtualDirectoryNode("network", "root", sysDir, null);
            sysDir.addChild(netDir);
            
            // /sys/kernel
            VirtualDirectoryNode kernelDir = new VirtualDirectoryNode("kernel", "root", sysDir, null);
            sysDir.addChild(kernelDir);
            kernelDir.addChild(new VirtualFileNode("version", "root", kernelDir, () -> "JavaOS v0.1.0-alpha\n"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
