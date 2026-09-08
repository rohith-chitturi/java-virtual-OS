package com.rohith.javavirtualos.kernel;

import com.rohith.javavirtualos.filesystem.model.FileMetadata;
import com.rohith.javavirtualos.filesystem.model.FileMode;
import com.rohith.javavirtualos.filesystem.model.Inode;

public class SecurityManager {

    public boolean canRead(User user, Inode node) {
        if ("root".equals(user.getUsername())) return true;

        FileMetadata metadata = node.getMetadata();
        FileMode mode = metadata.getMode();

        if (user.getUsername().equals(metadata.getOwner())) {
            return mode.canOwnerRead();
        } else if (user.getGroups().contains(metadata.getGroup())) {
            return mode.canGroupRead();
        } else {
            return mode.canOtherRead();
        }
    }

    public boolean canWrite(User user, Inode node) {
        if ("root".equals(user.getUsername())) return true;

        FileMetadata metadata = node.getMetadata();
        FileMode mode = metadata.getMode();

        if (user.getUsername().equals(metadata.getOwner())) {
            return mode.canOwnerWrite();
        } else if (user.getGroups().contains(metadata.getGroup())) {
            return mode.canGroupWrite();
        } else {
            return mode.canOtherWrite();
        }
    }

    public boolean canExecute(User user, Inode node) {
        if ("root".equals(user.getUsername())) return true;

        FileMetadata metadata = node.getMetadata();
        FileMode mode = metadata.getMode();

        if (user.getUsername().equals(metadata.getOwner())) {
            return mode.canOwnerExecute();
        } else if (user.getGroups().contains(metadata.getGroup())) {
            return mode.canGroupExecute();
        } else {
            return mode.canOtherExecute();
        }
    }
}
