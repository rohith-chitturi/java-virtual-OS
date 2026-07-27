package com.rohith.javavirtualos.kernel.network.socket;

import com.rohith.javavirtualos.kernel.network.IPAddress;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PortManager {
    private static class BindKey {
        final IPAddress ip;
        final int port;

        BindKey(IPAddress ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BindKey bindKey = (BindKey) o;
            return port == bindKey.port && Objects.equals(ip, bindKey.ip);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ip, port);
        }
    }

    private final Set<BindKey> boundPorts = new HashSet<>();
    private int nextEphemeralPort = 49152;

    public synchronized boolean bind(IPAddress ip, int port) {
        if (port < 1 || port > 65535) return false;
        BindKey key = new BindKey(ip, port);
        if (boundPorts.contains(key)) {
            return false;
        }
        boundPorts.add(key);
        return true;
    }

    public synchronized void unbind(IPAddress ip, int port) {
        boundPorts.remove(new BindKey(ip, port));
    }

    public synchronized int bindEphemeral(IPAddress ip) {
        int startPort = nextEphemeralPort;
        while (true) {
            int port = nextEphemeralPort++;
            if (nextEphemeralPort > 65535) nextEphemeralPort = 49152;
            
            if (bind(ip, port)) {
                return port;
            }
            if (nextEphemeralPort == startPort) {
                // All ephemeral ports exhausted!
                return -1;
            }
        }
    }
}
