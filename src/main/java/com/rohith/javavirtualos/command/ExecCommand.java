package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.kernel.process.manager.ProcessManager;
import com.rohith.javavirtualos.kernel.process.manager.ProcessTask;
import com.rohith.javavirtualos.kernel.process.runtime.ExecutableLoader;
import com.rohith.javavirtualos.kernel.process.runtime.ExecutionContext;
import com.rohith.javavirtualos.kernel.process.runtime.Instruction;
import com.rohith.javavirtualos.kernel.process.runtime.SystemCallInterface;
import com.rohith.javavirtualos.kernel.process.runtime.VirtualMachine;
import com.rohith.javavirtualos.services.ProcessService;

import java.util.List;

/**
 * Executes a .vexe file by parsing it and loading it into the VirtualMachine.
 */
public class ExecCommand extends AbstractCommand {

    private final ProcessService processService;
    private final FileSystemManager fsManager;

    public ExecCommand(ProcessService processService, FileSystemManager fsManager) {
        super("exec", "Executes a .vexe file in the Virtual Machine.", "exec <file.vexe>");
        this.processService = processService;
        this.fsManager = fsManager;
    }

    @Override
    public CommandResult execute(String[] args) {
        if (args.length < 1) {
            return CommandResult.failure("Usage: " + getUsage());
        }

        String path = args[0];
        try {
            FileNode node = fsManager.resolveFile(path);
            if (node == null || node.isDirectory()) {
                return CommandResult.failure("File not found or is a directory: " + path);
            }
            
            // Read source from VFS
            String source = new String(node.readData());
            List<String> sourceLines = List.of(source.split("\\r?\\n"));
            
            // Parse instructions
            List<Instruction> instructions = ExecutableLoader.parse(sourceLines);
            
            // We need a context. Let's just create a dummy context and VM for synchronous execution.
            // In a full implementation, this should spawn a background process via ProcessService.
            ExecutionContext ctx = new ExecutionContext(0);
            SystemCallInterface sys = new SystemCallInterface(processService.getManager(), null);
            VirtualMachine vm = new VirtualMachine(ctx, instructions, sys);
            
            // For now, run it synchronously. 
            // In the future, we will submit this as a ProcessTask to the ProcessManager.
            vm.run();
            
            return CommandResult.success("Program exited with code: " + vm.getExitCode());
            
        } catch (Exception e) {
            return CommandResult.failure("Execution failed: " + e.getMessage());
        }
    }
}
