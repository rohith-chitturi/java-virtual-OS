package com.rohith.javavirtualos.services;

import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.FileNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.shell.ShellContext;
import java.util.Collection;

public class DefaultFileSystemService implements FileSystemService {

    private final FileSystemManager manager;

    public DefaultFileSystemService(FileSystemManager manager) {
        this.manager = manager;
    }

    private DirectoryNode getCurrentDir(ShellContext context) throws FileSystemException {
        return manager.resolveDirectory(context.getCurrentDirectory(), manager.getRoot());
    }

    @Override
    public CommandResult listDirectory(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            DirectoryNode targetDir = path == null ? currentDir : manager.resolveDirectory(path, currentDir);
            
            StringBuilder sb = new StringBuilder();
            Collection<Inode> children = targetDir.getChildren();
            for (Inode child : children) {
                String type = child instanceof DirectoryNode ? "[DIR] " : "[FILE]";
                sb.append(String.format("%-7s %s%n", type, child.getName()));
            }
            return CommandResult.success(sb.toString().trim());
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult makeDirectory(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            manager.createDirectory(path, currentDir, "root");
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult removeDirectory(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            manager.remove(path, currentDir, true);
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult removeFile(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            manager.remove(path, currentDir, false);
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult changeDirectory(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            DirectoryNode targetDir = manager.resolveDirectory(path, currentDir);
            context.setCurrentDirectory(targetDir.getAbsolutePath());
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult touchFile(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            manager.createFile(path, currentDir, "root");
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult printTree(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            DirectoryNode targetDir = path == null ? currentDir : manager.resolveDirectory(path, currentDir);
            StringBuilder sb = new StringBuilder();
            buildTree(targetDir, 0, sb);
            return CommandResult.success(sb.toString().trim());
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    private void buildTree(DirectoryNode dir, int depth, StringBuilder sb) {
        String indent = "  ".repeat(depth);
        sb.append(indent).append(dir.getName().isEmpty() ? "/" : dir.getName()).append("\n");
        for (Inode child : dir.getChildren()) {
            if (child instanceof DirectoryNode) {
                buildTree((DirectoryNode) child, depth + 1, sb);
            } else {
                sb.append(indent).append("  ").append(child.getName()).append("\n");
            }
        }
    }

    @Override
    public CommandResult catFile(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            Inode node = manager.resolvePath(path, currentDir);
            if (node == null) return CommandResult.failure("File not found: " + path);
            if (!(node instanceof FileNode)) return CommandResult.failure(path + " is a directory");
            
            return CommandResult.success(((FileNode) node).getContent());
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult writeFile(String path, String content, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            Inode node = manager.resolvePath(path, currentDir);
            if (node == null) {
                manager.createFile(path, currentDir, "root");
                node = manager.resolvePath(path, currentDir);
            }
            if (!(node instanceof FileNode)) return CommandResult.failure(path + " is a directory");
            
            ((FileNode) node).setContent(content);
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult appendFile(String path, String content, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            Inode node = manager.resolvePath(path, currentDir);
            if (node == null) return CommandResult.failure("File not found: " + path);
            if (!(node instanceof FileNode)) return CommandResult.failure(path + " is a directory");
            
            ((FileNode) node).appendContent(content);
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }
}
