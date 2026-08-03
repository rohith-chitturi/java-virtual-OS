package com.rohith.javavirtualos.command.text;

import com.rohith.javavirtualos.command.Command;
import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.shell.stream.VirtualInput;
import com.rohith.javavirtualos.shell.stream.VirtualOutput;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HeadCommand implements Command {

    private final FileSystemService fsService;

    public HeadCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return execute(args, context, null, null);
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context, VirtualInput in, VirtualOutput out) {
        int linesToPrint = 10;
        String content = "";
        
        if (args.length > 0) {
            String filePath = args[0];
            CommandResult catResult = fsService.catFile(filePath, context);
            if (!catResult.isSuccess()) return catResult;
            content = catResult.getMessage();
        } else {
            if (in != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }
                    content = builder.toString();
                } catch (Exception e) {
                    return CommandResult.failure("head: error reading from input stream");
                }
            } else {
                return CommandResult.failure("head: missing input");
            }
        }

        String[] lines = content.split("\n");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(linesToPrint, lines.length); i++) {
            result.append(lines[i]).append("\n");
        }

        String finalResult = result.toString();
        if (finalResult.endsWith("\n")) {
            finalResult = finalResult.substring(0, finalResult.length() - 1);
        }
        return CommandResult.success(finalResult);
    }

    @Override
    public String getName() {
        return "head";
    }

    @Override
    public String getDescription() {
        return "Output the first part of files";
    }
}
