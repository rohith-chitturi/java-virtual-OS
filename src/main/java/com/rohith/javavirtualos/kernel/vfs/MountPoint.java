package com.rohith.javavirtualos.kernel.vfs;

public class MountPoint {
    private final Path path;
    private final VfsDirectory root;

    public MountPoint(Path path, VfsDirectory root) {
        this.path = path;
        this.root = root;
    }

    public Path getPath() { return path; }
    public VfsDirectory getRoot() { return root; }
}
