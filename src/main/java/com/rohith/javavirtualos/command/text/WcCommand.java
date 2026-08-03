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

public class WcCommand implements Command {

    private final FileSystemService fsService;

    public WcCommand(FileSystemService fsService) {
        this.fsService = fsService;
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return execute(args, context, null, null);
    }

    @Override
    public CommandResult execute(String[] args, ShellContext context, VirtualInput in, VirtualOutput out) {
        String content = "";
        
        if (args.length > 0) {
            String filePath = args[0];
            CommandResult catResult = fsService.catFile(filePath, context);
            if (!catResult.isSuccess()) {
                return catResult;
            }
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
                    return CommandResult.failure("wc: error reading from input stream");
                }
            } else {
                return CommandResult.failure("wc: missing input");
            }
        }

        int lines = content.isEmpty() ? 0 : content.split("\n").length;
        int words = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;
        int chars = content.length();

        return CommandResult.success(String.format("%d %d %d", lines, words, chars));
    }

    @Override
    public String getName() {
        return "wc";
    }

    @Override
    public String getDescription() {
        return "Word, line, and character count";
    }
}
