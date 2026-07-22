package com.rohith.javavirtualos.kernel.security;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.SecurityEvent.*;

import java.util.Optional;
import java.util.UUID;

public class SessionManager {
    private final SecurityManager securityManager;
    private final KernelEventBus eventBus;
    private Session activeSession;

    public SessionManager(SecurityManager securityManager, KernelEventBus eventBus) {
        this.securityManager = securityManager;
        this.eventBus = eventBus;
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = securityManager.authenticate(username, password);
        if (userOpt.isPresent()) {
            this.activeSession = new Session(UUID.randomUUID().toString(), userOpt.get());
            eventBus.publish(new LoginSuccessEvent(username));
            return true;
        }
        eventBus.publish(new LoginFailureEvent(username));
        return false;
    }

    public void logout() {
        this.activeSession = null;
    }

    public Optional<Session> getActiveSession() {
        return Optional.ofNullable(activeSession);
    }
}
