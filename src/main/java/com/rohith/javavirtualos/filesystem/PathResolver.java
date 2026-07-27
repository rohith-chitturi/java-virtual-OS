package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.Inode;

/**
 * Helper to resolve string paths (absolute or relative) into Inodes.
 */
public class PathResolver {
    
    private final DirectoryNode root;

    public PathResolver(DirectoryNode root) {
        this.root = root;
    }

    /**
     * Resolves a path to the corresponding Inode.
     */
    public Inode resolvePath(String path, DirectoryNode currentDir) {
        if (path == null || path.isEmpty()) {
            return currentDir;
        }

        DirectoryNode startNode = currentDir;
        
        // Handle absolute paths and home shortcut
        if (path.startsWith("/")) {
            startNode = root;
            path = path.substring(1); // strip leading slash
        } else if (path.startsWith("~")) {
            Inode resolvedHome = resolvePath("/home/javavm", root); // Assuming default home
            if (resolvedHome == null || !(resolvedHome instanceof DirectoryNode)) {
                startNode = root;
            } else {
                startNode = (DirectoryNode) resolvedHome;
            }
            path = path.length() > 1 ? path.substring(2) : "";
        }

        if (path.isEmpty()) {
            return startNode;
        }

        String[] parts = path.split("/");
        Inode currentNode = startNode;

        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }
            
            if (part.equals("..")) {
                if (currentNode.getParent() != null) {
                    currentNode = currentNode.getParent();
                }
                continue;
            }

            if (!(currentNode instanceof DirectoryNode dir)) {
                return null; // Path implies traversal but node is not a directory
            }

            Inode child = dir.getChild(part);
            if (child == null) {
                return null; // Not found
            }
            currentNode = child;
        }

        return currentNode;
    }
    
    /**
     * Helper to get the parent directory of a target path.
     * Example: "/usr/bin/java" -> returns the DirectoryNode for "/usr/bin".
     */
    public DirectoryNode resolveParentDirectory(String path, DirectoryNode currentDir) {
        if (path.equals("/")) return null; // Root has no parent
        
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            return currentDir; // Relative single token
        }
        if (lastSlash == 0 && path.length() == 1) {
            return root;
        }
        
        String parentPath = path.substring(0, lastSlash);
        if (parentPath.isEmpty()) {
            parentPath = "/";
        }
        
        Inode parentNode = resolvePath(parentPath, currentDir);
        if (parentNode instanceof DirectoryNode) {
            return (DirectoryNode) parentNode;
        }
        
        return null;
    }
    
    /**
     * Extracts just the final name component of a path.
     */
    public String extractName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash == -1 ? path : path.substring(lastSlash + 1);
    }
}
