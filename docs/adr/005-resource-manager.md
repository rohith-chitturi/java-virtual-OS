# ADR 005: Resource Manager

## Context
Processes need memory, CPU time, and file handles. Allowing `ProcessManager` to allocate memory directly violates the Single Responsibility Principle and complicates future features like paging or swap space.

## Decision
We established a `ResourceManager` that coordinates resource allocation. `ProcessManager` requests memory from it during process creation and releases it upon termination.

## Trade-offs and Consequences
- **Pros**: Clears the path for complex Memory Management in Phase 6. Centralized tracking prevents resource leaks.
- **Cons**: Additional layer of abstraction and potential synchronization bottleneck.
