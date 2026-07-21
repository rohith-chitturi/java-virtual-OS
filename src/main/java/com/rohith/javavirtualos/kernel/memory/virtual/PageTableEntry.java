package com.rohith.javavirtualos.kernel.memory.virtual;

public class PageTableEntry {
    private final Page page;
    private Frame frame;
    private boolean valid;
    private boolean dirty;
    private boolean referenced;
    private PageState state;

    public PageTableEntry(Page page) {
        this.page = page;
        this.frame = null;
        this.valid = false;
        this.dirty = false;
        this.referenced = false;
        this.state = PageState.NEVER_ALLOCATED;
    }

    public Page getPage() { return page; }
    public Frame getFrame() { return frame; }
    public void setFrame(Frame frame) { this.frame = frame; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public boolean isDirty() { return dirty; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
    
    public boolean isReferenced() { return referenced; }
    public void setReferenced(boolean referenced) { this.referenced = referenced; }
    
    public PageState getState() { return state; }
    public void setState(PageState state) { this.state = state; }
}
