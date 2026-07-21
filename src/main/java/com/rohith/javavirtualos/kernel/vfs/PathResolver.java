package com.rohith.javavirtualos.kernel.vfs;

import java.util.List;
import java.util.Optional;

public class PathResolver {
    private final MountPoint rootMount;

    public PathResolver(MountPoint rootMount) {
        this.rootMount = rootMount;
    }

    public Optional<VfsNode> resolve(Path path, VfsDirectory cwd) {
        VfsNode current = path.isAbsolute() ? rootMount.getRoot() : cwd;
        
        List<String> segments = path.getSegments();
        for (String segment : segments) {
            if (segment.equals("..")) {
                if (current.getParent() != null) {
                    current = current.getParent();
                }
            } else if (segment.equals(".")) {
                continue;
            } else {
                if (!current.isDirectory()) return Optional.empty();
                VfsDirectory dir = (VfsDirectory) current;
                Optional<VfsNode> child = dir.getChild(segment);
                if (child.isPresent()) {
                    current = child.get();
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(current);
    }
    
    public Optional<VfsDirectory> resolveParent(Path path, VfsDirectory cwd) {
        Path parentPath = path.getParent();
        if (parentPath == null) {
            return path.isAbsolute() ? Optional.of(rootMount.getRoot()) : Optional.of(cwd);
        }
        Optional<VfsNode> parentNode = resolve(parentPath, cwd);
        if (parentNode.isPresent() && parentNode.get().isDirectory()) {
            return Optional.of((VfsDirectory) parentNode.get());
        }
        return Optional.empty();
    }
}
