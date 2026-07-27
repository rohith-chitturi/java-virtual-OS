package com.rohith.javavirtualos.kernel.metrics;

import java.util.ArrayList;
import java.util.List;

public class ExecutionTimeline {
    private final List<TimelineEntry> timeline = new ArrayList<>();

    public synchronized void record(long tick, int pid) {
        timeline.add(new TimelineEntry(tick, pid));
    }

    public synchronized List<TimelineEntry> getTimeline() {
        return new ArrayList<>(timeline);
    }

    public static class TimelineEntry {
        public final long tick;
        public final int pid;
        
        public TimelineEntry(long tick, int pid) {
            this.tick = tick;
            this.pid = pid;
        }
    }
}
