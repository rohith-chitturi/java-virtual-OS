package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.FileSystemService;

import com.rohith.javavirtualos.kernel.process.runtime.ExecutableLoader;
import com.rohith.javavirtualos.kernel.process.runtime.ExecutionContext;
import com.rohith.javavirtualos.kernel.process.runtime.Instruction;
import com.rohith.javavirtualos.kernel.process.runtime.syscall.SystemCallDispatcher;
import com.rohith.javavirtualos.kernel.process.runtime.VirtualMachine;
import com.rohith.javavirtualos.services.ProcessService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.util.List;

/**
 * Executes a .vexe file by parsing it and loading it into the VirtualMachine.
 */
public class ExecCommand implements Command {

    private final ProcessService processService;
    private final FileSystemService fsService;
    private final com.rohith.javavirtualos.kernel.process.runtime.RuntimeStatistics stats;

    public ExecCommand(ProcessService processService, FileSystemService fsService, com.rohith.javavirtualos.kernel.process.runtime.RuntimeStatistics stats) {
        this.processService = processService;
        this.fsService = fsService;
        this.stats = stats;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 1) {
            return CommandResult.failure("Usage: exec <file.vexe>");
        }

        String path = args[0];
        boolean background = false;
        if (args[args.length - 1].equals("&")) {
            background = true;
        }

        try {
            CommandResult catResult = fsService.catFile(path, context);
            if (!catResult.isSuccess()) {
                return CommandResult.failure("Failed to read file: " + catResult.getMessage());
            }
            
            String source = catResult.getMessage();
            List<String> sourceLines = List.of(source.split("\\r?\\n"));
            
            // Parse instructions
            com.rohith.javavirtualos.kernel.process.runtime.Executable executable = ExecutableLoader.parse(sourceLines);
            if (stats != null) stats.incrementExecutablesLoaded();
            
            // Create VM context
            ExecutionContext ctx = new ExecutionContext(0);
            VirtualMachine vm = new VirtualMachine(ctx, executable, stats);
            
            // Create Process
            com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock pcb = processService.getManager().createProcess(executable.getName(), context.getCurrentUser(), null, 1);
            pcb.setVirtualMachine(vm);
            
            // Allocate File Descriptors (0: stdin, 1: stdout, 2: stderr)
            pcb.getFileDescriptorTable().allocate(new com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor(context.getIn()));
            pcb.getFileDescriptorTable().allocate(new com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor(context.getOut()));
            pcb.getFileDescriptorTable().allocate(new com.rohith.javavirtualos.kernel.process.descriptor.StreamDescriptor(context.getOut()));
            
            // Submit to OS
            processService.getManager().startProcess(pcb.getPid());
            processService.getDispatcher().submitProcess(pcb);
            
            if (background) {
                return CommandResult.success("[" + pcb.getPid() + "] " + executable.getName() + " started in background");
            } else {
                // Wait for process to finish
                com.rohith.javavirtualos.kernel.process.pcb.ExitStatus status = processService.getManager().waitProcess(1, pcb.getPid());
                return CommandResult.success("Program exited with code: " + status.getExitCode());
            }
            
        } catch (Exception e) {
            return CommandResult.failure("Execution failed: " + e.getMessage());
        }
    }


    @Override
    public String getName() {
        return "exec";
    }

    @Override
    public String getDescription() {
        return "Executes a .vexe file in the Virtual Machine.";
    }
}
