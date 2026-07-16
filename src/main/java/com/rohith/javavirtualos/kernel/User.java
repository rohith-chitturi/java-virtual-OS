package com.rohith.javavirtualos.kernel;

public class User {
    private final String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
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
