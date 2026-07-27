package com.rohith.javavirtualos.kernel.network;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoutingTable {
    // A simplified routing table matching prefixes to NetworkInterfaces
    private final Map<String, NetworkInterface> routes = new LinkedHashMap<>();

    public void addRoute(String cidrPrefix, NetworkInterface netIf) {
        routes.put(cidrPrefix, netIf);
    }

    public NetworkInterface route(IPAddress destination) {
        String ipStr = destination.getAddressString();
        // Simplified loopback routing for now
        if (ipStr.startsWith("127.")) {
            return routes.get("127.0.0.0/8");
        }
        // Exact match fallback (can be improved later with proper CIDR math)
        for (Map.Entry<String, NetworkInterface> entry : routes.entrySet()) {
            String routePrefix = entry.getKey().split("/")[0];
            if (ipStr.equals(routePrefix)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public Map<String, NetworkInterface> getRoutes() {
        return routes;
    }
}
