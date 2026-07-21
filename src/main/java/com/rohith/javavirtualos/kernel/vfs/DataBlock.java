package com.rohith.javavirtualos.kernel.vfs;

public class DataBlock {
    private byte[] data;

    public DataBlock(byte[] data) {
        this.data = data;
    }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    public int getSize() { return data != null ? data.length : 0; }
}
