package com.rohith.javavirtualos.kernel.vfs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileDescriptorTable {
    private final Map<Integer, OpenFile> descriptors;
    private int nextFd = 0;

    public FileDescriptorTable() {
        this.descriptors = new HashMap<>();
    }

    public int allocate(OpenFile file) {
        int fd = nextFd++;
        descriptors.put(fd, file);
        return fd;
    }
    
    public Optional<OpenFile> get(int fd) {
        return Optional.ofNullable(descriptors.get(fd));
    }
    
    public boolean close(int fd) {
        OpenFile file = descriptors.remove(fd);
        if (file != null) {
            file.decrementReference();
            return true;
        }
        return false;
    }
    
    public Map<Integer, OpenFile> getAll() {
        return descriptors;
    }
}
