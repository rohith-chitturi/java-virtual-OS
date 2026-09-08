package com.rohith.javavirtualos.kernel;

import java.util.HashSet;
import java.util.Set;

public class User {
    private final String username;
    private String password;
    private final Set<String> groups;
    private short umask;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.groups = new HashSet<>();
        // Default group for all users is "users"
        this.groups.add("users");
        this.umask = 0022;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getGroups() {
        return groups;
    }
    
    public void addGroup(String group) {
        this.groups.add(group);
    }

    public short getUmask() {
        return umask;
    }

    public void setUmask(short umask) {
        this.umask = umask;
    }

    public boolean authenticate(String password) {
        if (this.password == null) {
            return password == null || password.isEmpty();
        }
        return this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
