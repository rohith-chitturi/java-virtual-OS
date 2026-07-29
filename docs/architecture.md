# Java Virtual OS Architecture

This document provides a high-level overview of the major subsystems within the Java Virtual OS simulator. The architecture is designed to reflect real-world operating system concepts (like VFS, IPC, Networking, Memory Management, and Device Management) using clean, decoupled Java patterns (Events, Registries, and Facades).

## Device Management Subsystem

**Purpose:** Provides a unified hardware abstraction layer, allowing the virtual OS to interact with simulated devices (like `/dev/null`, `/dev/zero`, `/dev/random`) without exposing driver implementations directly to user-space code.

**Key Components:**
- `DeviceManager`: Coordinates device registration, VFS mounting, and global subsystem statistics.
- `DeviceRegistry`: Thread-safe registry mapping names to `DeviceDriver` instances.
- `DeviceDescriptor`: Contains metadata (vendor, capabilities, state) for a given device.
- `DeviceDriver`: Interface for simulated hardware.

**Architecture Diagram:**

```mermaid
flowchart TD
    DM[DeviceManager]
    DR[DeviceRegistry]
    DDDesc[DeviceDescriptor]
    DD[DeviceDriver]
    NullD[NullDevice]
    ZeroD[ZeroDevice]
    RandD[RandomDevice]
    DN[DeviceNode]
    VFS[Virtual File System]
    FDT[FileDescriptorTable]

    DM --> DR
    DR --> DDDesc
    DDDesc --> DD
    DD --> NullD
    DD --> ZeroD
    DD --> RandD
    
    NullD -.-> DN
    ZeroD -.-> DN
    RandD -.-> DN

    DN --> VFS
    VFS --> FDT
```

## Virtual File System (VFS)

**Purpose:** Provides a unified tree-based hierarchy for all files, directories, and devices, serving as the single entry point for I/O operations.

**Key Components:**
- `FileSystemManager`: Root coordinator of the file system.
- `PathResolver`: Translates absolute and relative paths to internal VFS nodes.
- `VfsNode` (Abstract): Base element representing a tree node.
- `DirectoryNode`, `FileNode`, `DeviceNode`: Concrete node implementations.

**Architecture Diagram:**

```mermaid
flowchart TD
    FSM[FileSystemManager]
    PR[PathResolver]
    VN[VfsNode]
    DirN[DirectoryNode]
    FileN[FileNode]
    DevN[DeviceNode]
    
    FSM --> PR
    PR --> VN
    VN <|-- DirN
    VN <|-- FileN
    VN <|-- DevN
```

## Networking Subsystem

**Purpose:** Simulates a TCP/IP networking stack allowing local inter-socket communication without binding to the host machine's actual network stack.

**Key Components:**
- `NetworkManager`: Routes packets internally based on routing tables.
- `VirtualSocket`: Descriptor type representing a simulated network connection.
- `PortManager`: Tracks bound ports to prevent collisions.

**Architecture Diagram:**

```mermaid
flowchart LR
    P1[Process 1]
    P2[Process 2]
    S1[Socket 1]
    S2[Socket 2]
    NM[NetworkManager]
    
    P1 --> S1
    P2 --> S2
    S1 -- Packet --> NM
    NM -- Packet --> S2
```

## Process Management & CPU Scheduling

**Purpose:** Simulates a multi-tasking kernel by managing process lifecycles (PID allocation, states, execution) and scheduling virtual CPU cycles.

**Key Components:**
- `ProcessManager`: Global registry of all virtual processes.
- `ProcessControlBlock (PCB)`: Metadata struct for a process (State, PID, UID, Memory Limits).
- `SchedulerSimulator`: Demonstrates CPU scheduling (e.g., Round Robin, FCFS).

**Architecture Diagram:**

```mermaid
flowchart TD
    PM[ProcessManager]
    PCB[ProcessControlBlock]
    FDT[FileDescriptorTable]
    MemSpace[VirtualMemorySpace]
    
    KD[KernelDispatcher]
    Proc[Processor]
    CPU0[CPU 0]
    CPU1[CPU 1]
    Sched0[Scheduler]
    Sched1[Scheduler]
    RQ0[Run Queue]
    RQ1[Run Queue]
    LB[LoadBalancer]
    
    PM --> PCB
    PCB --> KD
    PCB --> FDT
    PCB --> MemSpace
    
    KD --> Proc
    Proc --> CPU0
    Proc --> CPU1
    CPU0 --> Sched0
    CPU1 --> Sched1
    Sched0 --> RQ0
    Sched1 --> RQ1
    RQ0 --> LB
    RQ1 --> LB
```

**Process Lifecycle Diagram:**

```mermaid
flowchart TD
    NEW --> READY
    READY --> RUNNING
    RUNNING --> WAITING
    WAITING --> READY
    RUNNING --> READY
    RUNNING --> BLOCKED
    BLOCKED --> READY
    RUNNING --> SUSPENDED
    SUSPENDED --> READY
    RUNNING --> ZOMBIE
    ZOMBIE --> TERMINATED
    RUNNING --> TERMINATED
```

## Memory Management

**Purpose:** Simulates both physical memory (allocation, metrics) and virtual memory (MMU, paging, page faults).

**Key Components:**
**Key Components:**
- `MemoryManagementUnit`: Translates virtual to physical addresses, generates page faults.
- `FrameTable`: Tracks physical memory allocation.
- `SwapManager`: Evicts and loads pages to/from the VirtualFileSystem.
- `VirtualMemoryArea`: Logical memory regions mapped to processes.

**Architecture Diagram:**

```mermaid
flowchart TD
    Proc[Process]
    VMA[Virtual Memory Areas]
    PT[Page Table]
    MMU[Memory Management Unit]
    FT[Frame Table]
    SM[Swap Manager]
    VFS[Virtual File System]
    
    Proc --> VMA
    VMA --> PT
    PT --> MMU
    MMU --> FT
    FT --> SM
    SM --> VFS
```

---

*This document is auto-generated and maintained alongside the Java Virtual OS implementation.*
