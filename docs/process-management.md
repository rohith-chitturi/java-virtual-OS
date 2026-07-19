# Process Management

The Process Management subsystem is responsible for allocating resources, managing state, and handling the execution lifecycle of all processes within Java Virtual OS.

## Architecture
- **Process Control Block (PCB)**: The core data structure representing a process. It holds references to scheduling metrics, allocated resources, process state, and the executable task.
- **ProcessTask**: The actual `Runnable` implementation that contains a `volatile boolean running` flag, ensuring graceful termination rather than unsafe thread interrupts.
- **ProcessManager**: Coordinates process creation, state transitions, and resource cleanup.
- **KernelEventBus**: Broadcasts state changes for logging and system-wide visibility.
