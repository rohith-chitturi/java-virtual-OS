package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

public class IostatCommand implements Command {

    private final FileSystemService fsService;

    public IostatCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        CommandResult sysBlockRes = fsService.catFile("/sys/block/vdisk0", context);
        String vdiskInfo = sysBlockRes.isSuccess() ? sysBlockRes.getMessage().trim() : "Unknown";

        StringBuilder sb = new StringBuilder();
        sb.append("Linux 0.1.0-alpha (JavaOS) \n\n");
        sb.append(String.format("avg-cpu:  %%user   %%nice %%system %%iowait  %%steal   %%idle%n"));
        sb.append(String.format("           0.00    0.00    0.00    0.00    0.00  100.00%n%n"));
        sb.append(String.format("Device             tps    kB_read/s    kB_wrtn/s    kB_read    kB_wrtn%n"));
        sb.append(String.format("vdisk0            0.00         0.00         0.00          0          0   (%s)%n", vdiskInfo));
        
        return CommandResult.success(sb.toString().trim());
    }

    @Override
    public String getName() {
        return "iostat";
    }

    @Override
    public String getDescription() {
        return "Report Central Processing Unit (CPU) statistics and input/output statistics for devices and partitions";
    }
}
