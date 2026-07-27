package com.rohith.javavirtualos.kernel.process.descriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileDescriptorTable {
    private final Map<Integer, Descriptor> descriptors;
    private int nextFd;

    public FileDescriptorTable() {
        this.descriptors = new HashMap<>();
        this.nextFd = 0; // 0, 1, 2 could be reserved for stdin, stdout, stderr later
    }

    public int allocate(Descriptor descriptor) {
        int fd = nextFd++;
        descriptor.setFd(fd);
        descriptors.put(fd, descriptor);
        return fd;
    }

    public Optional<Descriptor> get(int fd) {
        return Optional.ofNullable(descriptors.get(fd));
    }

    public boolean close(int fd) {
        Descriptor desc = descriptors.remove(fd);
        if (desc != null) {
            desc.close();
            return true;
        }
        return false;
    }
}
