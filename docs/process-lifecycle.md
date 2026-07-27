# Process Lifecycle

Every process follows a strict state machine enforced by `ProcessStateMachine`.

## States
1. **NEW**: Process is created, resources are allocated, but the thread has not started.
2. **READY**: Process is ready to be executed by the CPU.
3. **RUNNING**: Process is currently executing instructions.
4. **WAITING**: Process is waiting for an I/O event.
5. **SUSPENDED**: Process is paused (e.g., via user request).
6. **BLOCKED**: Process is unable to acquire a necessary resource.
7. **TERMINATED**: Process has finished execution gracefully or was killed.

## Transitions
- `NEW` -> `READY`
- `READY` -> `RUNNING` or `SUSPENDED`
- `RUNNING` -> `WAITING`, `READY`, `BLOCKED`, or `TERMINATED`
- `SUSPENDED` -> `READY`, `WAITING`, or `TERMINATED`
