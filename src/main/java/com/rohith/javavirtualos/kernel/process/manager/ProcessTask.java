package com.rohith.javavirtualos.kernel.process.manager;

public class ProcessTask implements Runnable {
    private final Runnable executable;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private int exitCode = 0;

    public ProcessTask(Runnable executable) {
        this.executable = executable;
    }

    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                synchronized (this) {
                    while (paused && running) {
                        this.wait();
                    }
                }
                if (!running) break;

                executable.run();
                break; // Execute once for simple tasks
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            exitCode = 1;
        } finally {
            running = false;
        }
    }

    public void stop() {
        running = false;
        synchronized (this) {
            this.notifyAll(); // wake up if paused
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public boolean isRunning() { return running; }
    public int getExitCode() { return exitCode; }
}
