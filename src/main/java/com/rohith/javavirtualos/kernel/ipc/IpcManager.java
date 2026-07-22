package com.rohith.javavirtualos.kernel.ipc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IpcManager {
    private final Map<Long, IPCObject> ipcObjects;
    private final IPCStatistics stats;
    private long nextId = 1;

    public IpcManager(IPCStatistics stats) {
        this.ipcObjects = new HashMap<>();
        this.stats = stats;
    }

    public long register(IPCObject obj) {
        long id = nextId++;
        ipcObjects.put(id, obj);
        return id;
    }

    public void unregister(long id) {
        ipcObjects.remove(id);
    }

    public IPCObject get(long id) {
        return ipcObjects.get(id);
    }
    
    public Collection<IPCObject> getAll() {
        return Collections.unmodifiableCollection(ipcObjects.values());
    }
}
