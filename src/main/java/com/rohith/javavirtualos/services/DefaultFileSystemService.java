package com.rohith.javavirtualos.services;

import com.rohith.javavirtualos.command.CommandResult;
import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.FileSystemManager;
import com.rohith.javavirtualos.filesystem.model.DirectoryEntry;
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
            Collection<DirectoryEntry> entries = targetDir.getEntries();
            for (DirectoryEntry entry : entries) {
                String type = entry.getInode() instanceof DirectoryNode ? "[DIR] " : "[FILE]";
                sb.append(String.format("%-7s %s%n", type, entry.getName()));
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
            manager.createDirectory(path, currentDir, context.getCurrentUser());
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult removeDirectory(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            manager.remove(path, currentDir, context.getCurrentDirectory(), true, context.getCurrentUser());
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult removeFile(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            manager.remove(path, currentDir, context.getCurrentDirectory(), false, context.getCurrentUser());
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
            // Need absolute path. We can construct it by resolving parts or simple string manipulation.
            String newPath;
            if (path.startsWith("/")) {
                newPath = path;
            } else if (path.equals("..")) {
                String c = context.getCurrentDirectory();
                if (c.equals("/")) newPath = "/";
                else {
                    int last = c.lastIndexOf('/');
                    newPath = last <= 0 ? "/" : c.substring(0, last);
                }
            } else if (path.equals(".")) {
                newPath = context.getCurrentDirectory();
            } else {
                String c = context.getCurrentDirectory();
                newPath = c.equals("/") ? "/" + path : c + "/" + path;
            }
            // A proper path normalization could be done here, but this is a placeholder.
            context.setCurrentDirectory(newPath);
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult touchFile(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            manager.createFile(path, currentDir, context.getCurrentUser());
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
            String rootName = path == null ? context.getCurrentDirectory() : path;
            int lastSlash = rootName.lastIndexOf('/');
            String baseName = lastSlash == -1 ? rootName : rootName.substring(lastSlash + 1);
            if (baseName.isEmpty()) baseName = "/";
            buildTree(targetDir, baseName, 0, sb);
            return CommandResult.success(sb.toString().trim());
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    private void buildTree(DirectoryNode dir, String name, int depth, StringBuilder sb) {
        String indent = "  ".repeat(depth);
        sb.append(indent).append(name.isEmpty() ? "/" : name).append("\n");
        for (DirectoryEntry entry : dir.getEntries()) {
            if (entry.getInode() instanceof DirectoryNode) {
                buildTree((DirectoryNode) entry.getInode(), entry.getName(), depth + 1, sb);
            } else {
                sb.append(indent).append("  ").append(entry.getName()).append("\n");
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
            
            manager.validateReadAccess(node, context.getCurrentUser());
            return CommandResult.success(((FileNode) node).getContent());
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult readExecutable(String path, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            Inode node = manager.resolvePath(path, currentDir);
            if (node == null) return CommandResult.failure("File not found: " + path);
            if (!(node instanceof FileNode)) return CommandResult.failure(path + " is a directory");
            
            manager.validateExecuteAccess(node, context.getCurrentUser());
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
                manager.createFile(path, currentDir, context.getCurrentUser());
                node = manager.resolvePath(path, currentDir);
            }
            if (!(node instanceof FileNode)) return CommandResult.failure(path + " is a directory");
            
            manager.validateWriteAccess(node, context.getCurrentUser());
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
            
            manager.validateWriteAccess(node, context.getCurrentUser());
            ((FileNode) node).appendContent(content);
            return CommandResult.success();
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    @Override
    public CommandResult findFile(String path, String pattern, ShellContext context) {
        try {
            DirectoryNode currentDir = getCurrentDir(context);
            DirectoryNode targetDir = path == null ? currentDir : manager.resolveDirectory(path, currentDir);
            StringBuilder sb = new StringBuilder();
            String rootPath = path == null ? context.getCurrentDirectory() : (path.startsWith("/") ? path : context.getCurrentDirectory() + "/" + path);
            findRecursive(targetDir, rootPath, pattern, sb);
            return CommandResult.success(sb.toString().trim());
        } catch (FileSystemException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    private void findRecursive(DirectoryNode dir, String currentPath, String pattern, StringBuilder sb) {
        for (DirectoryEntry entry : dir.getEntries()) {
            if (entry.getName().contains(pattern)) {
                String fullPath = currentPath.equals("/") ? "/" + entry.getName() : currentPath + "/" + entry.getName();
                sb.append(fullPath).append("\n");
            }
            if (entry.getInode() instanceof DirectoryNode) {
                String nextPath = currentPath.equals("/") ? "/" + entry.getName() : currentPath + "/" + entry.getName();
                findRecursive((DirectoryNode) entry.getInode(), nextPath, pattern, sb);
            }
        }
    }
}
