package com.rohith.javavirtualos.services;

import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;

public interface ProcessService {
    CommandResult listProcesses(ShellContext context);
    CommandResult killProcess(int pid, ShellContext context);
    void executeAsProcess(String name, Runnable task, ShellContext context);
    com.rohith.javavirtualos.kernel.process.scheduler.KernelDispatcher getDispatcher();
    com.rohith.javavirtualos.kernel.process.manager.ProcessManager getManager();
}
