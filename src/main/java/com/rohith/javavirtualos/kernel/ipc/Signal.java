package com.rohith.javavirtualos.kernel.ipc;

public enum Signal {
    SIGINT(2, "Interrupt"),
    SIGKILL(9, "Kill"),
    SIGTERM(15, "Terminate"),
    SIGUSR1(10, "User-defined 1");

    private final int number;
    private final String description;

    Signal(int number, String description) {
        this.number = number;
        this.description = description;
    }

    public int getNumber() { return number; }
    public String getDescription() { return description; }
}
