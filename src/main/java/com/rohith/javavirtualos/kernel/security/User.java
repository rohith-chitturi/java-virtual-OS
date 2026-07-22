package com.rohith.javavirtualos.kernel.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {
    private final int uid;
    private final String username;
    private String passwordHash;
    private final Role role;
    private final int primaryGroupId;
    private final Set<Integer> secondaryGroupIds;

    public User(int uid, String username, String plainPassword, Role role, int primaryGroupId) {
        this.uid = uid;
        this.username = username;
        this.passwordHash = hashPassword(plainPassword);
        this.role = role;
        this.primaryGroupId = primaryGroupId;
        this.secondaryGroupIds = new HashSet<>();
    }

    public int getUid() { return uid; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public int getPrimaryGroupId() { return primaryGroupId; }
    public Set<Integer> getSecondaryGroupIds() { return Collections.unmodifiableSet(secondaryGroupIds); }
    
    public void addSecondaryGroup(int gid) { secondaryGroupIds.add(gid); }
    public void removeSecondaryGroup(int gid) { secondaryGroupIds.remove(gid); }

    public boolean verifyPassword(String plainText) {
        return this.passwordHash.equals(hashPassword(plainText));
    }
    
    public void changePassword(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }

    private String hashPassword(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }
}
