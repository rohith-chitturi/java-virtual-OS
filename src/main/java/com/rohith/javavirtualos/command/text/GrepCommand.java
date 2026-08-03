package com.rohith.javavirtualos.command.text;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.shell.stream.VirtualInput;
import com.rohith.javavirtualos.shell.stream.VirtualOutput;
import com.rohith.javavirtualos.services.FileSystemService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GrepCommand implements Command {

    private final FileSystemService fsService;

    public GrepCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return execute(args, context, null, null);
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context, VirtualInput in, VirtualOutput out) {
        if (args.length == 0) {
            return CommandResult.failure("grep: missing pattern");
        }

        String pattern = args[0];
        StringBuilder result = new StringBuilder();

        if (args.length > 1) {
            // Read from file
            String filePath = args[1];
            CommandResult catResult = fsService.catFile(filePath, context);
            if (!catResult.isSuccess()) {
                return catResult; // File not found or error
            }
            String content = catResult.getMessage();
            for (String line : content.split("\n")) {
                if (line.contains(pattern)) {
                    result.append(line).append("\n");
                }
            }
        } else {
            // Read from VirtualInput (pipe)
            if (in != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(pattern)) {
                            result.append(line).append("\n");
                        }
                    }
                } catch (Exception e) {
                    return CommandResult.failure("grep: error reading from input stream: " + e.getMessage());
                }
            } else {
                return CommandResult.failure("grep: missing input");
            }
        }

        String finalResult = result.toString();
        if (finalResult.endsWith("\n")) {
            finalResult = finalResult.substring(0, finalResult.length() - 1);
        }
        return CommandResult.success(finalResult);
    }

    @Override
    public String getName() {
        return "grep";
    }

    @Override
    public String getDescription() {
        return "Search for a pattern in text";
    }
}
