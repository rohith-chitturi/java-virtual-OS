package com.rohith.javavirtualos.kernel.security;

import java.time.Instant;

public class Session {
    private final String sessionId;
    private final User user;
    private final Instant loginTime;

    public Session(String sessionId, User user) {
        this.sessionId = sessionId;
        this.user = user;
        this.loginTime = Instant.now();
    }

    public String getSessionId() { return sessionId; }
    public User getUser() { return user; }
    public Instant getLoginTime() { return loginTime; }
}
