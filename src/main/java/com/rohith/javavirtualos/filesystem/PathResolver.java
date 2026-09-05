package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.exceptions.TooManySymlinksException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.filesystem.model.SymlinkNode;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Helper to resolve string paths (absolute or relative) into Inodes.
 */
public class PathResolver {
    
    private final DirectoryNode root;
    private static final int MAX_SYMLINK_DEPTH = 10;

    public PathResolver(DirectoryNode root) {
        this.root = root;
    }

    public Inode resolvePath(String path, DirectoryNode currentDir) throws FileSystemException {
        return resolvePathInternal(path, currentDir, 0, new HashSet<>());
    }

    private Inode resolvePathInternal(String path, DirectoryNode currentDir, int depth, Set<Long> visitedSymlinks) throws FileSystemException {
        if (depth >= MAX_SYMLINK_DEPTH) {
            throw new TooManySymlinksException(path);
        }

        if (path == null || path.isEmpty()) {
            return currentDir;
        }

        Stack<Inode> pathStack = new Stack<>();
        
        DirectoryNode startNode = currentDir;
        
        // Handle absolute paths and home shortcut
        if (path.startsWith("/")) {
            startNode = root;
            path = path.substring(1); // strip leading slash
        } else if (path.startsWith("~")) {
            Inode resolvedHome = resolvePathInternal("/home/javavm", root, depth, visitedSymlinks); // Assuming default home
            if (resolvedHome == null || !(resolvedHome instanceof DirectoryNode)) {
                startNode = root;
            } else {
                startNode = (DirectoryNode) resolvedHome;
            }
            path = path.length() > 1 ? path.substring(2) : "";
        }

        pathStack.push(startNode);

        if (path.isEmpty()) {
            return startNode;
        }

        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }
            
            if (part.equals("..")) {
                if (pathStack.size() > 1) { // Never pop the root of our traversal context
                    pathStack.pop();
                }
                continue;
            }

            Inode currentNode = pathStack.peek();
            if (!(currentNode instanceof DirectoryNode dir)) {
                return null; // Path implies traversal but node is not a directory
            }

            Inode child = dir.getChild(part);
            if (child == null) {
                return null; // Not found
            }

            if (child instanceof SymlinkNode symlink) {
                if (visitedSymlinks.contains(symlink.getInodeId())) {
                    throw new TooManySymlinksException("Circular symlink detected at " + part);
                }
                visitedSymlinks.add(symlink.getInodeId());
                
                String targetPath = symlink.getTargetPath();
                // Resolve the target relative to the directory containing the symlink
                Inode resolvedTarget = resolvePathInternal(targetPath, dir, depth + 1, visitedSymlinks);
                if (resolvedTarget == null) {
                    return null; // Dangling link during traversal
                }
                pathStack.push(resolvedTarget);
            } else {
                pathStack.push(child);
            }
        }

        return pathStack.peek();
    }
    
    /**
     * Helper to get the parent directory of a target path.
     * Example: "/usr/bin/java" -> returns the DirectoryNode for "/usr/bin".
     */
    public DirectoryNode resolveParentDirectory(String path, DirectoryNode currentDir) throws FileSystemException {
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
