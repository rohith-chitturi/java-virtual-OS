package com.rohith.javavirtualos.kernel.events.boot;

public class FileSystemMountedEvent extends BootEvent {
    private final String mountPath;

    public FileSystemMountedEvent(String mountPath) {
        this.mountPath = mountPath;
    }

    @Override
    public String getMessage() {
        return "File system mounted successfully at " + mountPath + ".";
    }
}
