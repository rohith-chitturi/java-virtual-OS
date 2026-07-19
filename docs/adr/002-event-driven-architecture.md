# ADR 002: Event-Driven Architecture

## Context
Various kernel subsystems (Process Manager, File System, Memory) needed to notify the system of state changes (e.g., a process terminating, a file being created) for logging and potential triggering of other systems without introducing tight coupling.

## Decision
We implemented a `KernelEventBus` supporting a strongly-typed `KernelEvent` hierarchy (e.g., `ProcessStateChangedEvent`). Subsystems publish events to the bus rather than directly calling other managers.

## Trade-offs and Consequences
- **Pros**: Loose coupling between subsystems. Extremely extensible for adding a GUI or advanced logging later.
- **Cons**: Event flow can be harder to trace during debugging compared to direct method calls.
