package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.exceptions.TooManySymlinksException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import com.rohith.javavirtualos.filesystem.model.Inode;
import com.rohith.javavirtualos.filesystem.model.SymlinkNode;
import com.rohith.javavirtualos.kernel.SecurityManager;
import com.rohith.javavirtualos.kernel.User;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class PathResolver {
    
    private final DirectoryNode root;
    private static final int MAX_SYMLINK_DEPTH = 10;

    public PathResolver(DirectoryNode root) {
        this.root = root;
    }

    public Inode resolvePath(String path, DirectoryNode currentDir, User user, SecurityManager securityManager) throws FileSystemException {
        return resolvePathInternal(path, currentDir, user, securityManager, 0, new HashSet<>());
    }

    private Inode resolvePathInternal(String path, DirectoryNode currentDir, User user, SecurityManager securityManager, int depth, Set<Long> visitedSymlinks) throws FileSystemException {
        if (depth >= MAX_SYMLINK_DEPTH) {
            throw new TooManySymlinksException(path);
        }

        if (path == null || path.isEmpty()) {
            return currentDir;
        }

        Stack<Inode> pathStack = new Stack<>();
        
        DirectoryNode startNode = currentDir;
        
        if (path.startsWith("/")) {
            startNode = root;
            path = path.substring(1); 
        } else if (path.startsWith("~")) {
            Inode resolvedHome = resolvePathInternal("/home/javavm", root, user, securityManager, depth, visitedSymlinks); 
            if (resolvedHome == null || !(resolvedHome instanceof DirectoryNode)) {
                startNode = root;
            } else {
                startNode = (DirectoryNode) resolvedHome;
            }
            path = path.length() > 1 ? path.substring(2) : "";
        }

        // Before pushing startNode (which is a directory), we must be allowed to "execute" it to traverse it.
        // Wait, currentDir or root should be already verified to reach it, but to traverse down from it, we check.
        // POSIX checks execute permission on the directories *as* they are traversed.
        
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
                if (pathStack.size() > 1) { 
                    pathStack.pop();
                }
                continue;
            }

            Inode currentNode = pathStack.peek();
            if (!(currentNode instanceof DirectoryNode dir)) {
                return null; 
            }
            
            if (securityManager != null && !securityManager.canExecute(user, dir)) {
                throw new FileSystemException("Permission denied");
            }

            Inode child = dir.getChild(part);
            if (child == null) {
                return null; 
            }

            if (child instanceof SymlinkNode symlink) {
                if (visitedSymlinks.contains(symlink.getInodeId())) {
                    throw new TooManySymlinksException("Circular symlink detected at " + part);
                }
                visitedSymlinks.add(symlink.getInodeId());
                
                String targetPath = symlink.getTargetPath();
                Inode resolvedTarget = resolvePathInternal(targetPath, dir, user, securityManager, depth + 1, visitedSymlinks);
                if (resolvedTarget == null) {
                    return null; 
                }
                pathStack.push(resolvedTarget);
            } else {
                pathStack.push(child);
            }
        }

        return pathStack.peek();
    }
    
    public DirectoryNode resolveParentDirectory(String path, DirectoryNode currentDir, User user, SecurityManager securityManager) throws FileSystemException {
        if (path.equals("/")) return null; 
        
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            return currentDir; 
        }
        if (lastSlash == 0 && path.length() == 1) {
            return root;
        }
        
        String parentPath = path.substring(0, lastSlash);
        if (parentPath.isEmpty()) {
            parentPath = "/";
        }
        
        Inode parentNode = resolvePath(parentPath, currentDir, user, securityManager);
        if (parentNode instanceof DirectoryNode) {
            return (DirectoryNode) parentNode;
        }
        
        return null;
    }
    
    public String extractName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash == -1 ? path : path.substring(lastSlash + 1);
    }
}
