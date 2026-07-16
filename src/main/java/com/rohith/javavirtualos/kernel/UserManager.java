package com.rohith.javavirtualos.kernel;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> users;

    public UserManager() {
        this.users = new HashMap<>();
        // Default users
        this.users.put("root", new User("root", ""));
        this.users.put("guest", new User("guest", ""));
    }

    public boolean addUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        return true;
    }

    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.authenticate(password)) {
            return user;
        }
        return null;
    }

    public User getUser(String username) {
        return users.get(username);
    }
}
