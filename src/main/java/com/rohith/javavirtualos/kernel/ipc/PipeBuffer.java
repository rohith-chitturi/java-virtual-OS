package com.rohith.javavirtualos.kernel.ipc;

public class PipeBuffer {
    private final byte[] buffer;
    private int head = 0;
    private int tail = 0;
    private int size = 0;
    private final int capacity;

    public PipeBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new byte[capacity];
    }

    public boolean write(byte b) {
        if (size == capacity) return false;
        buffer[tail] = b;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    public Integer read() {
        if (size == 0) return null;
        byte b = buffer[head];
        head = (head + 1) % capacity;
        size--;
        return (int) b;
    }
    
    public boolean isFull() { return size == capacity; }
    public boolean isEmpty() { return size == 0; }
}
