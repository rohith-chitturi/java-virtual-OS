package com.rohith.javavirtualos.kernel.network;

import java.util.Objects;

public class IPAddress {
    public static final IPAddress LOOPBACK = new IPAddress("127.0.0.1");
    public static final IPAddress ANY = new IPAddress("0.0.0.0");
    public static final IPAddress BROADCAST = new IPAddress("255.255.255.255");

    private final String address;
    private final byte[] octets;

    public byte[] getOctets() {
        return octets;
    }


    public IPAddress(String address) {
        if (!isValid(address)) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + address);
        }
        this.address = address;
        this.octets = parseOctets(address);
    }

    public static boolean isValid(String address) {
        if (address == null || address.isEmpty()) return false;
        String[] parts = address.split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private byte[] parseOctets(String address) {
        String[] parts = address.split("\\.");
        byte[] octets = new byte[4];
        for (int i = 0; i < 4; i++) {
            octets[i] = (byte) Integer.parseInt(parts[i]);
        }
        return octets;
    }
    
    public String getAddressString() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPAddress ipAddress = (IPAddress) o;
        return Objects.equals(address, ipAddress.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return address;
    }
}
