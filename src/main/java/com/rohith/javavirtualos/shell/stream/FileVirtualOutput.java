package com.rohith.javavirtualos.shell.stream;

import com.rohith.javavirtualos.services.FileSystemService;
import com.rohith.javavirtualos.shell.ShellContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

public class FileVirtualOutput extends VirtualOutput {


    private final ByteArrayOutputStream outputStream;

    public FileVirtualOutput(String filePath, boolean append, FileSystemService fsService, ShellContext shellContext) {
        super(filePath);
        this.outputStream = new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                String content = this.toString(StandardCharsets.UTF_8);
                if (append) {
                    fsService.appendFile(filePath, content, shellContext);
                } else {
                    fsService.writeFile(filePath, content, shellContext);
                }
            }
        };
    }

    @Override
    public java.io.PrintStream getPrintStream() {
        return new java.io.PrintStream(outputStream);
    }

    @Override
    public void close() {
        try { outputStream.close(); } catch (Exception ignored) {}
    }
}
