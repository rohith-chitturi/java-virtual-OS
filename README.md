# Java Virtual OS

A Complete Operating System Simulation built purely in Java.

## Features
- **Virtual Shell**: A command-line interface simulating a Linux terminal.
- **Kernel & Services**: A central kernel coordinating system managers via Dependency Injection.
- **Event Bus**: Event-driven communication for loose coupling.
- *(Coming Soon)* Virtual File System, Process Scheduling, Memory Management, and Security.

## Requirements
- Java 21 or higher
- Maven 3.8+

## Build and Run
```bash
mvn clean package
java -jar target/java-virtual-os-0.1.0-alpha.jar
```

## Architecture
See `docs/architecture.md` for a complete overview.
