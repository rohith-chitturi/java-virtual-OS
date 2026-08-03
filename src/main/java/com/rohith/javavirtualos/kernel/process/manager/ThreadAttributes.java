package com.rohith.javavirtualos.kernel.process.manager;

public class ThreadAttributes {
    private boolean detached;
    private int priority;
    
    public ThreadAttributes() {
        this.detached = false;
        this.priority = 5;
    }
    
    public boolean isDetached() { return detached; }
    public void setDetached(boolean detached) { this.detached = detached; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
