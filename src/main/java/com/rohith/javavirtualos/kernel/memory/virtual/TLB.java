package com.rohith.javavirtualos.kernel.memory.virtual;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class TLB {
    private final int capacity;
    private final Map<Page, Frame> cache;

    public TLB(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<Page, Frame>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Page, Frame> eldest) {
                return size() > capacity;
            }
        };
    }

    public Optional<Frame> lookup(Page page) {
        return Optional.ofNullable(cache.get(page));
    }

    public void update(Page page, Frame frame) {
        cache.put(page, frame);
    }

    public void invalidate(Page page) {
        cache.remove(page);
    }

    public void flush() {
        cache.clear();
    }
    
    public Map<Page, Frame> getEntries() {
        return cache;
    }
}
