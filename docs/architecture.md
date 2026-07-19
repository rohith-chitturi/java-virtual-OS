# Architecture

## High-level Architecture
Java Virtual OS uses Clean Architecture and SOLID principles. The **Kernel** acts as the central coordinator and single source of truth, managing and initializing all sub-managers. It uses Dependency Injection to provide necessary services up to the Shell layer.

## Module Responsibilities
- **Kernel**: Core coordinator, bootloader, handles DI, manages overall OS state (SystemContext).
- **Shell**: The CLI parser and execution environment. Calls Services, not Kernel directly.
- **Services**: Abstraction layer between Shell and Kernel Managers.
- **Managers**: Logic components for specific subsystems (FileSystemManager, ProcessManager).
- **Models**: Data representations (Process, VirtualFile).
- **Plugins**: Dynamic runtime loading of external commands.
- **Events**: Loose coupling via EventBus for system-wide notifications.

## Request Flow
1. User enters command in Shell (`JavaOS> echo hello`).
2. `ShellParser` tokenizes input.
3. `CommandDispatcher` looks up `EchoCommand` in `CommandRegistry`.
4. Command is executed with `ShellContext` and `Services`.
5. Service interacts with Kernel Managers if necessary.

## Boot Sequence
`JavaVirtualOS (Main)` -> `BootLoader` -> `Kernel` -> Managers Init -> `Shell` Start

## OS Simulation Roadmap
This project follows a 12-phase roadmap to build a fully-fledged Operating System Simulation Platform:

- Phase 1: Kernel & Boot
- Phase 2: Virtual File System
- Phase 3: Users & Security
- Phase 4: Process Management
- Phase 5: CPU Scheduling
- Phase 6: Memory Management
- Phase 7: Virtual Memory & Paging
- Phase 8: IPC (Pipes, Shared Memory)
- Phase 9: Deadlock Detection
- Phase 10: Networking
- Phase 11: Device Management
- Phase 12: JavaFX Desktop Environment
