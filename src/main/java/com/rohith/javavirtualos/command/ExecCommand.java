package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.services.FileSystemService;

import com.rohith.javavirtualos.kernel.process.runtime.ExecutableLoader;
import com.rohith.javavirtualos.kernel.process.runtime.ExecutionContext;
import com.rohith.javavirtualos.kernel.process.runtime.Instruction;
import com.rohith.javavirtualos.kernel.process.runtime.SystemCallInterface;
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

    public ExecCommand(ProcessService processService, FileSystemService fsService) {
        this.processService = processService;
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        if (args.length < 1) {
            return CommandResult.failure("Usage: exec <file.vexe>");
        }

        String path = args[0];
        try {
            CommandResult catResult = fsService.catFile(path, context);
            if (!catResult.isSuccess()) {
                return CommandResult.failure("Failed to read file: " + catResult.getMessage());
            }
            
            String source = catResult.getMessage();
            List<String> sourceLines = List.of(source.split("\\r?\\n"));
            
            // Parse instructions
            List<Instruction> instructions = ExecutableLoader.parse(sourceLines);
            
            // We need a context. Let's just create a dummy context and VM for synchronous execution.
            // In a full implementation, this should spawn a background process via ProcessService.
            ExecutionContext ctx = new ExecutionContext(0);
            SystemCallInterface sys = new SystemCallInterface(processService.getManager(), null);
            VirtualMachine vm = new VirtualMachine(ctx, instructions, sys);
            
            // For now, run it synchronously. 
            vm.run();
            
            return CommandResult.success("Program exited with code: " + vm.getExitCode());
            
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
