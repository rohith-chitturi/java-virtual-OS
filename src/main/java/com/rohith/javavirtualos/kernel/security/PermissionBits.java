package com.rohith.javavirtualos.kernel.security;

public class PermissionBits {
    private int bits;

    public PermissionBits(int bits) {
        if (bits < 0 || bits > 0777) {
            throw new IllegalArgumentException("Invalid permission bits: " + Integer.toOctalString(bits));
        }
        this.bits = bits;
    }

    public static PermissionBits fromOctal(String octalStr) {
        return new PermissionBits(Integer.parseInt(octalStr, 8));
    }

    public int getBits() { return bits; }

    public void setBits(int bits) {
        if (bits < 0 || bits > 0777) {
            throw new IllegalArgumentException("Invalid permission bits: " + Integer.toOctalString(bits));
        }
        this.bits = bits;
    }

    public boolean ownerCan(AccessMode mode) {
        return canAccess((bits >> 6) & 7, mode);
    }

    public boolean groupCan(AccessMode mode) {
        return canAccess((bits >> 3) & 7, mode);
    }

    public boolean othersCan(AccessMode mode) {
        return canAccess(bits & 7, mode);
    }

    private boolean canAccess(int mask, AccessMode mode) {
        switch (mode) {
            case READ: return (mask & 4) != 0;
            case WRITE: return (mask & 2) != 0;
            case EXECUTE: return (mask & 1) != 0;
            default: return false;
        }
    }

    @Override
    public String toString() {
        return String.format("0%03o", bits);
    }
}
