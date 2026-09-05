package com.rohith.javavirtualos.kernel.events;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * A bounded ring buffer capturing critical kernel events for dmesg and /proc/kmsg.
 */
public class KernelLogBuffer implements Consumer<KernelEvent> {

    private final LinkedList<String> buffer;
    private final int capacity;
    private long messageCount = 0;

    public KernelLogBuffer(int capacity, KernelEventBus eventBus) {
        this.buffer = new LinkedList<>();
        this.capacity = capacity;
        
        if (eventBus != null) {
            eventBus.subscribe(com.rohith.javavirtualos.kernel.events.ProcessCreatedEvent.class, this::accept);
            eventBus.subscribe(com.rohith.javavirtualos.kernel.events.ProcessStateChangedEvent.class, this::accept);
            eventBus.subscribe(com.rohith.javavirtualos.kernel.events.boot.KernelBootStartedEvent.class, this::accept);
            eventBus.subscribe(com.rohith.javavirtualos.kernel.events.boot.KernelReadyEvent.class, this::accept);
            eventBus.subscribe(com.rohith.javavirtualos.kernel.events.boot.FileSystemMountedEvent.class, this::accept);
            eventBus.subscribe(com.rohith.javavirtualos.kernel.events.boot.NetworkInitializedEvent.class, this::accept);
            eventBus.subscribe(com.rohith.javavirtualos.kernel.events.boot.MemoryInitializedEvent.class, this::accept);
        }
    }

    public synchronized void log(String message) {
        if (buffer.size() >= capacity) {
            buffer.removeFirst(); // discard oldest
        }
        buffer.addLast("[" + (++messageCount) + "] " + message);
    }

    @Override
    public void accept(KernelEvent event) {
        log("Event: " + event.getClass().getSimpleName() + " - " + event.toString());
    }

    public synchronized String getContents() {
        StringBuilder sb = new StringBuilder();
        for (String msg : buffer) {
            sb.append(msg).append("\n");
        }
        return sb.toString();
    }
}
