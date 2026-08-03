package com.rohith.javavirtualos.shell.stream;

import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;
import com.rohith.javavirtualos.command.CommandResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileVirtualInput extends VirtualInput {

    private final String filePath;
    private final FileSystemService fsService;
    private final ShellContext shellContext;
    private InputStream inputStream;

    public FileVirtualInput(String filePath, FileSystemService fsService, ShellContext shellContext) {
        super(filePath);
        this.filePath = filePath;
        this.fsService = fsService;
        this.shellContext = shellContext;
    }

    @Override
    public InputStream getInputStream() {
        if (inputStream == null) {
            CommandResult result = fsService.catFile(filePath, shellContext);
            if (result.isSuccess()) {
                inputStream = new ByteArrayInputStream(result.getMessage().getBytes(StandardCharsets.UTF_8));
            } else {
                // If file not found, return empty stream
                inputStream = new ByteArrayInputStream(new byte[0]);
            }
        }
        return inputStream;
    }

    @Override
    public void close() {
        if (inputStream != null) {
            try { inputStream.close(); } catch (Exception ignored) {}
        }
    }
}
