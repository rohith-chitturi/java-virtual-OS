# Process Table

The Virtual OS process table tracks all active and suspended processes.

## The `ps` Command
Typing `ps` in the shell outputs the process table in a standard, professional format:

```text
PID    NAME         STATE        PRI   MEM(KB)
1      Shell        RUNNING      5     0
2      SleepCommand RUNNING      5     0
```

## The `pstree` Command
Typing `pstree` outputs the hierarchical relationships of processes based on their `parentPid`:

```text
Shell (PID 1)
├── SleepCommand (PID 2)
├── Editor (PID 3)
```
