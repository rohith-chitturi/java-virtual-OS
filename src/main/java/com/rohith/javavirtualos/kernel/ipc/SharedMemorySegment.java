package com.rohith.javavirtualos.kernel.ipc;

import com.rohith.javavirtualos.kernel.memory.virtual.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharedMemorySegment implements IPCObject {
    private final long id;
    private final String name;
    private final List<Frame> frames;

    public SharedMemorySegment(long id, String name, int numFrames) {
        this.id = id;
        this.name = name;
        this.frames = new ArrayList<>(numFrames);
    }
    
    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    @Override public long getId() { return id; }
    @Override public String getName() { return name; }
    
    public List<Frame> getFrames() {
        return Collections.unmodifiableList(frames);
    }
}
