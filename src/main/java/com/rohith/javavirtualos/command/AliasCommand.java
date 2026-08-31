package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.shell.stream.VirtualInput;
import com.rohith.javavirtualos.shell.stream.VirtualOutput;

import java.util.Map;

public class AliasCommand implements Command {

    @Override
    public String getName() {
        return "alias";
    }

    @Override
    public String getDescription() {
        return "Define or display aliases";
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return execute(args, context, null, null);
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context, VirtualInput in, VirtualOutput out) {
        if (args.length == 0) {
            // Display all aliases
            if (context.getAliases().isEmpty()) {
                return CommandResult.success("");
            }
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : context.getAliases().entrySet()) {
                sb.append("alias ").append(entry.getKey()).append("='").append(entry.getValue()).append("'\n");
            }
            return CommandResult.success(sb.toString().trim());
        }

        for (String arg : args) {
            int eqIdx = arg.indexOf('=');
            if (eqIdx == -1) {
                // display specific alias
                String val = context.getAliases().get(arg);
                if (val != null) {
                    if (out != null) {
                        out.getPrintStream().println("alias " + arg + "='" + val + "'");
                    } else {
                        context.getOut().println("alias " + arg + "='" + val + "'");
                    }
                } else {
                    return CommandResult.failure("alias: " + arg + ": not found");
                }
            } else {
                // set alias
                String name = arg.substring(0, eqIdx);
                String value = arg.substring(eqIdx + 1);
                
                // Remove surrounding quotes if present
                if ((value.startsWith("'") && value.endsWith("'")) || 
                    (value.startsWith("\"") && value.endsWith("\""))) {
                    value = value.substring(1, value.length() - 1);
                }
                
                context.getAliases().put(name, value);
            }
        }
        
        return CommandResult.success("");
    }
}
