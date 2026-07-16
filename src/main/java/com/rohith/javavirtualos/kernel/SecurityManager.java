package com.rohith.javavirtualos.kernel;

import com.rohith.javavirtualos.filesystem.model.Inode;

public class SecurityManager {

    public boolean canRead(User user, Inode node) {
        if ("root".equals(user.getUsername())) return true;
        // Everyone can read by default in this simple OS unless we add proper chmod later
        return true; 
    }

    public boolean canWrite(User user, Inode node) {
        if ("root".equals(user.getUsername())) return true;
        return user.getUsername().equals(node.getMetadata().getOwner());
    }

    public boolean canExecute(User user, Inode node) {
        if ("root".equals(user.getUsername())) return true;
        return user.getUsername().equals(node.getMetadata().getOwner());
    }
}
