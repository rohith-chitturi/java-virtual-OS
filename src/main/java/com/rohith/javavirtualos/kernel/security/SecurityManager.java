package com.rohith.javavirtualos.kernel.security;

import com.rohith.javavirtualos.kernel.events.KernelEventBus;
import com.rohith.javavirtualos.kernel.events.SecurityEvent.*;
import com.rohith.javavirtualos.kernel.vfs.INode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SecurityManager {
    private final Map<Integer, User> usersByUid = new HashMap<>();
    private final Map<String, User> usersByName = new HashMap<>();
    private final Map<Integer, Group> groupsByGid = new HashMap<>();
    private final KernelEventBus eventBus;
    
    private int nextUid = 1000;
    private int nextGid = 1000;

    public SecurityManager(KernelEventBus eventBus) {
        this.eventBus = eventBus;
        
        User root = new User(0, "root", "root", Role.ROOT, 0);
        Group rootGroup = new Group(0, "root");
        rootGroup.addMember(0);
        
        usersByUid.put(0, root);
        usersByName.put("root", root);
        groupsByGid.put(0, rootGroup);
    }

    public User createUser(String username, String password) {
        if (usersByName.containsKey(username)) {
            throw new IllegalArgumentException("User already exists");
        }
        int uid = nextUid++;
        int gid = nextGid++;
        
        Group group = new Group(gid, username);
        group.addMember(uid);
        groupsByGid.put(gid, group);
        
        User user = new User(uid, username, password, Role.USER, gid);
        usersByUid.put(uid, user);
        usersByName.put(username, user);
        
        eventBus.publish(new UserCreatedEvent(username));
        return user;
    }

    public void deleteUser(String username) {
        if ("root".equals(username)) throw new IllegalArgumentException("Cannot delete root");
        User user = usersByName.remove(username);
        if (user != null) {
            usersByUid.remove(user.getUid());
            eventBus.publish(new UserDeletedEvent(username));
        }
    }

    public Optional<User> authenticate(String username, String password) {
        User user = usersByName.get(username);
        if (user != null && user.verifyPassword(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
    
    public Optional<User> getUser(String username) {
        return Optional.ofNullable(usersByName.get(username));
    }
    
    public Optional<User> getUser(int uid) {
        return Optional.ofNullable(usersByUid.get(uid));
    }
    
    public Optional<Group> getGroup(int gid) {
        return Optional.ofNullable(groupsByGid.get(gid));
    }

    public boolean canAccess(User user, INode inode, AccessMode mode) {
        if (user.getRole() == Role.ROOT) return true;
        
        PermissionBits perms = inode.getPermissions();
        
        if (inode.getOwnerUid() == user.getUid()) {
            return perms.ownerCan(mode);
        }
        
        if (inode.getOwnerGid() == user.getPrimaryGroupId() || user.getSecondaryGroupIds().contains(inode.getOwnerGid())) {
            return perms.groupCan(mode);
        }
        
        return perms.othersCan(mode);
    }
    
    public boolean canSignal(User sourceUser, User targetUser) {
        if (sourceUser.getRole() == Role.ROOT) return true;
        return sourceUser.getUid() == targetUser.getUid();
    }
}
