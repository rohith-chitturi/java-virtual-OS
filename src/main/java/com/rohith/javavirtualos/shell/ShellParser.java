package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.shell.stream.PipeStream;
import com.rohith.javavirtualos.shell.stream.VirtualInput;
import com.rohith.javavirtualos.shell.stream.VirtualOutput;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.CommandRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShellParser {

    private final CommandRegistry registry;
    private final ShellContext context;
    private final com.rohith.javavirtualos.services.FileSystemService fsService;

    public ShellParser(CommandRegistry registry, ShellContext context, com.rohith.javavirtualos.services.FileSystemService fsService) {
        this.registry = registry;
        this.context = context;
        this.fsService = fsService;
    }

    public void executeLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        // Support for && and || can be added by splitting the line here
        // For now, we focus on | (pipes)
        
        String[] pipelineParts = line.split("\\|");
        
        List<ParsedCommand> commands = new ArrayList<>();
        
        for (String part : pipelineParts) {
            String expandedPart = expandAliases(part.trim());
            ParsedCommand cmd = parseSingleCommand(expandedPart);
            if (cmd != null) {
                commands.add(cmd);
            } else {
                context.getOut().println("Invalid command near: " + part);
                return;
            }
        }
        
        if (commands.isEmpty()) return;
        
        if (commands.size() == 1) {
            executePipelineCommand(commands.get(0), null, null, true);
            return;
        }
        
        // Execute pipeline
        try {
            VirtualInput currentIn = null;
            
            for (int i = 0; i < commands.size(); i++) {
                ParsedCommand cmd = commands.get(i);
                boolean isLast = (i == commands.size() - 1);
                
                VirtualOutput nextOut = null;
                VirtualInput nextIn = null;
                
                if (!isLast) {
                    PipeStream pipe = new PipeStream("pipe_" + i);
                    nextOut = pipe.getOutputEnd();
                    nextIn = pipe.getInputEnd();
                }
                
                executePipelineCommand(cmd, currentIn, nextOut, isLast);
                
                currentIn = nextIn;
            }
        } catch (IOException e) {
            context.getOut().println("Failed to setup pipeline: " + e.getMessage());
        }
    }
    
    private void executePipelineCommand(ParsedCommand cmd, VirtualInput in, VirtualOutput out, boolean isLast) {
        VirtualInput finalIn = in;
        VirtualOutput finalOut = out;
        
        if (cmd.redirectIn != null) {
            finalIn = new com.rohith.javavirtualos.shell.stream.FileVirtualInput(cmd.redirectIn, fsService, context);
        }
        if (cmd.redirectOut != null) {
            finalOut = new com.rohith.javavirtualos.shell.stream.FileVirtualOutput(cmd.redirectOut, cmd.appendOut, fsService, context);
        }
        
        final VirtualInput exeIn = finalIn;
        final VirtualOutput exeOut = finalOut;
        
        if (!isLast) {
            new Thread(() -> {
                executeSingle(cmd, exeIn, exeOut);
            }).start();
        } else {
            executeSingle(cmd, exeIn, exeOut);
        }
    }
    
    private void executeSingle(ParsedCommand cmd, VirtualInput in, VirtualOutput out) {
        Command command = registry.getCommand(cmd.name);
        if (command == null) {
            context.getOut().println(cmd.name + ": command not found");
            return;
        }
        try {
            CommandResult result = command.execute(cmd.args, context, in, out);
            if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                if (out != null) {
                    out.getPrintStream().write((result.getMessage() + "\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    out.getPrintStream().close();
                } else {
                    context.getOut().println(result.getMessage());
                }
            } else if (out != null) {
                out.getPrintStream().close();
            }
            if (result.shouldTerminateShell()) {
                // Handle shell termination if needed
            }
        } catch (Exception e) {
            context.getOut().println("Error executing command: " + e.getMessage());
        }
    }

    private String expandAliases(String part) {
        if (part == null || part.isEmpty()) return part;
        
        String[] tokens = part.split("\\s+", 2);
        String cmdName = tokens[0];
        
        java.util.Set<String> expanded = new java.util.HashSet<>();
        String currentName = cmdName;
        
        // Prevent infinite recursion by tracking expanded aliases
        while (context.getAliases().containsKey(currentName)) {
            if (expanded.contains(currentName)) {
                break; // Circular reference detected
            }
            expanded.add(currentName);
            
            String aliasValue = context.getAliases().get(currentName);
            // We only expand the first word recursively if it's just a simple alias chain
            String[] aliasTokens = aliasValue.split("\\s+", 2);
            currentName = aliasTokens[0];
            
            // Reconstruct the full command
            if (tokens.length > 1) {
                part = aliasValue + " " + tokens[1];
                tokens = part.split("\\s+", 2);
            } else {
                part = aliasValue;
            }
        }
        
        return part;
    }

    private ParsedCommand parseSingleCommand(String part) {
        if (part.isEmpty()) return null;
        
        List<String> tokens = new ArrayList<>();
        String[] rawTokens = part.split("\\s+");
        
        String redirectIn = null;
        String redirectOut = null;
        boolean appendOut = false;
        
        for (int i = 0; i < rawTokens.length; i++) {
            String t = rawTokens[i];
            if (t.equals(">")) {
                if (i + 1 < rawTokens.length) redirectOut = rawTokens[++i];
            } else if (t.equals(">>")) {
                if (i + 1 < rawTokens.length) {
                    redirectOut = rawTokens[++i];
                    appendOut = true;
                }
            } else if (t.equals("<")) {
                if (i + 1 < rawTokens.length) redirectIn = rawTokens[++i];
            } else {
                tokens.add(t);
            }
        }
        
        if (tokens.isEmpty()) return null;
        
        String name = tokens.get(0);
        
        if (name.startsWith("./") || name.endsWith(".vexe")) {
            tokens.add(0, "exec");
            name = "exec";
        }
        
        String[] args = new String[tokens.size() - 1];
        for (int i = 1; i < tokens.size(); i++) {
            args[i - 1] = tokens.get(i);
        }
        return new ParsedCommand(name, args, redirectIn, redirectOut, appendOut);
    }
    
    private static class ParsedCommand {
        String name;
        String[] args;
        String redirectIn;
        String redirectOut;
        boolean appendOut;
        
        ParsedCommand(String name, String[] args, String redirectIn, String redirectOut, boolean appendOut) {
            this.name = name;
            this.args = args;
            this.redirectIn = redirectIn;
            this.redirectOut = redirectOut;
            this.appendOut = appendOut;
        }
    }
}
