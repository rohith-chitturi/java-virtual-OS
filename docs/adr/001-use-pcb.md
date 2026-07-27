# ADR 001: Use Process Control Block (PCB)

## Context
As the virtual OS scaled, the single `VirtualProcess` class became overly bloated, attempting to manage execution state, metadata, and resource tracking. This tight coupling hindered the addition of advanced scheduling and IPC.

## Decision
We introduced a decoupled `ProcessControlBlock` (PCB) that acts as the central data structure. It isolates scheduling data (`SchedulingInfo`), resource tracking (`ResourceInfo`), and the raw executable logic (`ProcessTask`).

## Trade-offs and Consequences
- **Pros**: Clean separation of concerns. Mirrors real-world kernel architectures (e.g., Linux `task_struct`). Prepares the system for advanced modular CPU Schedulers.
- **Cons**: Minor increase in object instantiation and memory overhead per process.
