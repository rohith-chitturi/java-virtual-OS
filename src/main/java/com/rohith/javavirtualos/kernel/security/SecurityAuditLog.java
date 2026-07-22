package com.rohith.javavirtualos.kernel.security;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.SecurityEvent;
import com.rohith.javavirtualos.kernel.events.SecurityEvent.*;

import java.util.ArrayList;
import java.util.List;

public class SecurityAuditLog {
    private final List<String> logs = new ArrayList<>();

    public SecurityAuditLog(KernelEventBus bus) {
        bus.subscribe(LoginSuccessEvent.class, this::onEvent);
        bus.subscribe(LoginFailureEvent.class, this::onEvent);
        bus.subscribe(PermissionDeniedEvent.class, this::onEvent);
        bus.subscribe(UserCreatedEvent.class, this::onEvent);
        bus.subscribe(UserDeletedEvent.class, this::onEvent);
    }

    private void onEvent(SecurityEvent event) {
        logs.add(java.time.Instant.now() + " - " + event.getMessage());
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }
}
