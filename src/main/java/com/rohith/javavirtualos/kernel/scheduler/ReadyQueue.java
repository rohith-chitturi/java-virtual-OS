package com.rohith.javavirtualos.kernel.scheduler;

import com.rohith.javavirtualos.kernel.process.pcb.ProcessControlBlock;

import java.util.ArrayList;
import java.util.List;

public class ReadyQueue {
    private final List<ProcessControlBlock> queue = new ArrayList<>();

    public void add(ProcessControlBlock pcb) { 
        if (!queue.contains(pcb)) {
            queue.add(pcb); 
        }
    }
    
    public void remove(ProcessControlBlock pcb) { 
        queue.remove(pcb); 
    }
    
    public boolean isEmpty() { return queue.isEmpty(); }
    public int size() { return queue.size(); }
    public List<ProcessControlBlock> getQueue() { return new ArrayList<>(queue); }
}
