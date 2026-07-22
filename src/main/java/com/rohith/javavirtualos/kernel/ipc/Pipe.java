package com.rohith.javavirtualos.kernel.ipc;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

public class Pipe implements IPCObject {
    private final long id;
    private final String name;
    private final PipeBuffer buffer;
    private final WakeupManager wakeupManager;
    private ProcessControlBlock blockedReader;
    private ProcessControlBlock blockedWriter;

    public Pipe(long id, String name, int capacity, WakeupManager wakeupManager) {
        this.id = id;
        this.name = name;
        this.buffer = new PipeBuffer(capacity);
        this.wakeupManager = wakeupManager;
    }

    @Override public long getId() { return id; }
    @Override public String getName() { return name; }

    public boolean write(byte b, ProcessControlBlock writerPcb) {
        if (buffer.isFull()) {
            this.blockedWriter = writerPcb;
            wakeupManager.blockProcess(writerPcb);
            return false;
        }
        
        buffer.write(b);
        
        if (blockedReader != null && !buffer.isEmpty()) {
            wakeupManager.wakeupProcess(blockedReader);
            blockedReader = null;
        }
        
        return true;
    }

    public Integer read(ProcessControlBlock readerPcb) {
        if (buffer.isEmpty()) {
            this.blockedReader = readerPcb;
            wakeupManager.blockProcess(readerPcb);
            return null;
        }
        
        Integer data = buffer.read();
        
        if (blockedWriter != null && !buffer.isFull()) {
            wakeupManager.wakeupProcess(blockedWriter);
            blockedWriter = null;
        }
        
        return data;
    }
}
