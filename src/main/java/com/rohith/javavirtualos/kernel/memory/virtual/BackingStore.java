package com.rohith.javavirtualos.kernel.memory.virtual;

import java.util.HashSet;
import java.util.Set;

public class BackingStore {
    private final Set<Page> swappedPages = new HashSet<>();

    public void store(Page page) {
        swappedPages.add(page);
    }

    public boolean load(Page page) {
        return swappedPages.contains(page);
    }

    public void remove(Page page) {
        swappedPages.remove(page);
    }
}
