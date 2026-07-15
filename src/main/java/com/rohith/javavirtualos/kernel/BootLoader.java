package com.rohith.javavirtualos.kernel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Responsible for the startup sequence of the virtual OS.
 */
public class BootLoader {

    public void boot() {
        printBanner();
        System.out.println("Booting Java Virtual OS...\n");

        Kernel kernel = new Kernel();
        
        System.out.println("[ OK ] Loading Kernel");
        kernel.initialize();

        System.out.println("\n---------------------------------------------------------");
        printSystemInfo(kernel.getSystemContext());
        System.out.println(" Type 'help' to view available commands.");
        System.out.println("---------------------------------------------------------\n");

        // After initialization, start the shell loop
        kernel.startShell();
    }

    private void printBanner() {
        try (InputStream is = getClass().getResourceAsStream("/banner.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("=========================================================");
            System.out.println("                 JAVA VIRTUAL OS");
            System.out.println("=========================================================\n");
        }
    }

    private void printSystemInfo(SystemContext context) {
        System.out.println(" " + context.getOsName());
        System.out.println(" Version : " + context.getOsVersion());
        System.out.println(" Build   : " + context.getOsBuild());
        System.out.println(" Java    : " + context.getJavaTarget());
        System.out.println(" Status  : " + context.getOsStatus());
    }
}
