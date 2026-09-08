package com.rohith.javavirtualos.filesystem.model;

/**
 * Immutable representation of standard POSIX 9-bit permissions.
 */
public class FileMode {
    
    private final short mode;

    public FileMode(short mode) {
        // Only keep the lowest 9 bits
        this.mode = (short) (mode & 0777);
    }

    public static FileMode fromOctalString(String octal) {
        return new FileMode(Short.parseShort(octal, 8));
    }

    public short getMode() {
        return mode;
    }

    public boolean canOwnerRead() { return (mode & 0400) != 0; }
    public boolean canOwnerWrite() { return (mode & 0200) != 0; }
    public boolean canOwnerExecute() { return (mode & 0100) != 0; }

    public boolean canGroupRead() { return (mode & 0040) != 0; }
    public boolean canGroupWrite() { return (mode & 0020) != 0; }
    public boolean canGroupExecute() { return (mode & 0010) != 0; }

    public boolean canOtherRead() { return (mode & 0004) != 0; }
    public boolean canOtherWrite() { return (mode & 0002) != 0; }
    public boolean canOtherExecute() { return (mode & 0001) != 0; }

    public String toOctalString() {
        return String.format("%04o", mode);
    }

    public String toSymbolicString(FileType type) {
        StringBuilder sb = new StringBuilder();
        
        switch (type) {
            case DIRECTORY: sb.append('d'); break;
            case LINK: sb.append('l'); break;
            case DEVICE: sb.append('c'); break;
            case FILE: default: sb.append('-'); break;
        }

        sb.append(canOwnerRead() ? 'r' : '-');
        sb.append(canOwnerWrite() ? 'w' : '-');
        sb.append(canOwnerExecute() ? 'x' : '-');

        sb.append(canGroupRead() ? 'r' : '-');
        sb.append(canGroupWrite() ? 'w' : '-');
        sb.append(canGroupExecute() ? 'x' : '-');

        sb.append(canOtherRead() ? 'r' : '-');
        sb.append(canOtherWrite() ? 'w' : '-');
        sb.append(canOtherExecute() ? 'x' : '-');

        return sb.toString();
    }
}
