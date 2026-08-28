package com.rohith.javavirtualos;

import com.rohith.javavirtualos.kernel.BootLoader;
import com.rohith.javavirtualos.kernel.Kernel;

/**
 * The main entry point for the Java Virtual OS.
 */
public class JavaVirtualOS {

    public static void main(String[] args) {
        // Delegate to BootLoader to begin the OS startup sequence
        Kernel kernel = new BootLoader().boot();
        kernel.startShell();
    }
}
