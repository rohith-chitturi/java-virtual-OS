# CPU Schedulers

This directory contains the implementations for the OS CPU Scheduling algorithms.

## Active Implementations
- `fcfs`: First-Come, First-Serve scheduling.
- `roundrobin`: Time-slice based scheduling (uses `kernel.quantum`).
- `priority`: Preemptive priority scheduling.

## Future Algorithms
The following algorithms are planned for future phases and will implement the `Scheduler` interface. Placeholder directories have not been created to avoid empty class overhead.
- **SJF** (Shortest Job First)
- **SRTF** (Shortest Remaining Time First)
- **MLFQ** (Multilevel Feedback Queue)
- **EDF** (Earliest Deadline First)
