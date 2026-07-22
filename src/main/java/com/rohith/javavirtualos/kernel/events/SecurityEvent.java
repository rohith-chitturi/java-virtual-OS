package com.rohith.javavirtualos.kernel.events;

public abstract class SecurityEvent implements KernelEvent {
    public abstract String getMessage();
    
    public static class LoginSuccessEvent extends SecurityEvent {
        private final String username;
        public LoginSuccessEvent(String username) { this.username = username; }
        @Override public String getMessage() { return "Login Success: " + username; }
    }

    public static class LoginFailureEvent extends SecurityEvent {
        private final String username;
        public LoginFailureEvent(String username) { this.username = username; }
        @Override public String getMessage() { return "Login Failure: " + username; }
    }

    public static class PermissionDeniedEvent extends SecurityEvent {
        private final String resource;
        private final String username;
        public PermissionDeniedEvent(String username, String resource) {
            this.username = username;
            this.resource = resource;
        }
        @Override public String getMessage() { return "Permission Denied for " + username + " on " + resource; }
    }

    public static class UserCreatedEvent extends SecurityEvent {
        private final String username;
        public UserCreatedEvent(String username) { this.username = username; }
        @Override public String getMessage() { return "User Created: " + username; }
    }

    public static class UserDeletedEvent extends SecurityEvent {
        private final String username;
        public UserDeletedEvent(String username) { this.username = username; }
        @Override public String getMessage() { return "User Deleted: " + username; }
    }
}
