package com.rohith.javavirtualos.kernel.vfs;

import java.util.HashSet;
import java.util.Set;

public class VfsValidator {
    public void validate(VirtualFileSystem vfs) {
        MountPoint rootMount = vfs.getRootMount();
        VfsDirectory root = rootMount.getRoot();
        
        Set<INode> seenInodes = new HashSet<>();
        Set<VfsNode> seenNodes = new HashSet<>();
        
        validateNode(root, root, seenInodes, seenNodes);
    }
    
    private void validateNode(VfsNode node, VfsDirectory parent, Set<INode> seenInodes, Set<VfsNode> seenNodes) {
        if (node == null) throw new IllegalStateException("Null node encountered");
        
        if (!seenNodes.add(node)) {
            throw new IllegalStateException("Cyclic graph detected at node: " + node.getName());
        }
        
        if (!node.getName().equals("/") && node.getParent() != parent) {
            throw new IllegalStateException("Invalid parent reference for node: " + node.getName());
        }
        
        INode inode = node.getINode();
        if (inode == null) {
            throw new IllegalStateException("Null INode for node: " + node.getName());
        }
        seenInodes.add(inode);
        
        if (node.isDirectory()) {
            VfsDirectory dir = (VfsDirectory) node;
            Set<String> childNames = new HashSet<>();
            for (VfsNode child : dir.getChildren()) {
                if (!childNames.add(child.getName())) {
                    throw new IllegalStateException("Duplicate child name " + child.getName() + " in dir " + dir.getName());
                }
                validateNode(child, dir, seenInodes, seenNodes);
            }
        }
    }
}
