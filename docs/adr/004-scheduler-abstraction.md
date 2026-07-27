# ADR 004: Scheduler Abstraction

## Context
Process management involves deciding *which* process to run next. Baking the scheduling logic directly into `ProcessManager` would tightly couple the process lifecycle with one specific scheduling algorithm (e.g., Round Robin).

## Decision
We introduced a `Scheduler` interface. The `ProcessManager` delegates the decision of "what runs next" to this interface, allowing the concrete implementation to be swapped via `kernel.properties`.

## Trade-offs and Consequences
- **Pros**: High modularity. Supports swapping algorithms (FCFS, SJF, RR) at runtime or boot time.
- **Cons**: Requires careful concurrency management when passing PCBs back and forth between the Manager and the Scheduler.
