package com.rohith.javavirtualos.kernel.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Group {
    private final int gid;
    private final String groupName;
    private final Set<Integer> memberUids;

    public Group(int gid, String groupName) {
        this.gid = gid;
        this.groupName = groupName;
        this.memberUids = new HashSet<>();
    }

    public int getGid() { return gid; }
    public String getGroupName() { return groupName; }
    public Set<Integer> getMemberUids() { return Collections.unmodifiableSet(memberUids); }
    
    public void addMember(int uid) { memberUids.add(uid); }
    public void removeMember(int uid) { memberUids.remove(uid); }
}
