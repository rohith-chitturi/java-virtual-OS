package com.rohith.javavirtualos.kernel.events;

import com.rohith.javavirtualos.kernel.vfs.Path;

public abstract class VfsEvent extends KernelEvent {
    public abstract String getMessage();
    
    public static class FileCreatedEvent extends VfsEvent {
        private final Path path;
        public FileCreatedEvent(Path path) { this.path = path; }
        @Override public String getMessage() { return "File Created: " + path; }
    }

    public static class FileModifiedEvent extends VfsEvent {
        private final Path path;
        public FileModifiedEvent(Path path) { this.path = path; }
        @Override public String getMessage() { return "File Modified: " + path; }
    }

    public static class FileDeletedEvent extends VfsEvent {
        private final Path path;
        public FileDeletedEvent(Path path) { this.path = path; }
        @Override public String getMessage() { return "File Deleted: " + path; }
    }

    public static class DirectoryCreatedEvent extends VfsEvent {
        private final Path path;
        public DirectoryCreatedEvent(Path path) { this.path = path; }
        @Override public String getMessage() { return "Directory Created: " + path; }
    }

    public static class DirectoryDeletedEvent extends VfsEvent {
        private final Path path;
        public DirectoryDeletedEvent(Path path) { this.path = path; }
        @Override public String getMessage() { return "Directory Deleted: " + path; }
    }

    public static class FileOpenedEvent extends VfsEvent {
        private final Path path;
        public FileOpenedEvent(Path path) { this.path = path; }
        @Override public String getMessage() { return "File Opened: " + path; }
    }

    public static class FileClosedEvent extends VfsEvent {
        private final Path path;
        public FileClosedEvent(Path path) { this.path = path; }
        @Override public String getMessage() { return "File Closed: " + path; }
    }
}
