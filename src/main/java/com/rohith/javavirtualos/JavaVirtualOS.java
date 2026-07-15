package com.rohith.javavirtualos;

import com.rohith.javavirtualos.kernel.BootLoader;

/**
 * The main entry point for the Java Virtual OS.
 */
public class JavaVirtualOS {

    public static void main(String[] args) {
        // Delegate to BootLoader to begin the OS startup sequence
        new BootLoader().boot();
    }
}
