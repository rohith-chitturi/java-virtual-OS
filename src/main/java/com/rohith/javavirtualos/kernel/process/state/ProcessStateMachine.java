package com.rohith.javavirtualos.kernel.process.state;

import com.rohith.javavirtualos.kernel.exceptions.InvalidProcessStateException;

public class ProcessStateMachine {

    public static void validateTransition(ProcessState oldState, ProcessState newState) {
        boolean valid = false;
        switch (oldState) {
            case NEW:
                if (newState == ProcessState.READY) valid = true;
                break;
            case READY:
                if (newState == ProcessState.RUNNING || newState == ProcessState.SUSPENDED) valid = true;
                break;
            case RUNNING:
                if (newState == ProcessState.WAITING || newState == ProcessState.READY || newState == ProcessState.TERMINATED || newState == ProcessState.BLOCKED || newState == ProcessState.SUSPENDED) valid = true;
                break;
            case WAITING:
            case BLOCKED:
                if (newState == ProcessState.READY || newState == ProcessState.SUSPENDED) valid = true;
                break;
            case SUSPENDED:
                if (newState == ProcessState.READY || newState == ProcessState.WAITING || newState == ProcessState.TERMINATED) valid = true;
                break;
            case TERMINATED:
                // No transitions allowed out of terminated
                break;
        }
        
        if (!valid) {
            throw new InvalidProcessStateException(
                String.format("Invalid state transition from %s to %s", oldState, newState)
            );
        }
    }
}
