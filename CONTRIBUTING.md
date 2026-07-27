# Contributing to Java Virtual OS

Welcome! This project is structured as a production-grade kernel simulation.

## Core Philosophies
1. **Clean Architecture**: Follow SOLID principles. Subsystems must be decoupled using Dependency Injection via the `Kernel` class.
2. **Event-Driven**: Avoid tight coupling. Emit strongly-typed `KernelEvent` objects to the `KernelEventBus` for cross-module communication.
3. **Architectural Purity**: Utilize the Process Control Block (PCB) for state. Do not put executable logic into data models.

## Package Organization
- All core operating system features must be nested under `com.rohith.javavirtualos.kernel`.
- **Architecture Freeze**: Do not restructure or move existing legacy packages (e.g., `shell`, `filesystem`) unless explicitly requested as part of a dedicated refactoring milestone.

## Branch Strategy
- Create feature branches following the format: `feature/phase-X-feature-name`.
- Commit atomically and push.
- Open Pull Requests for all changes.
