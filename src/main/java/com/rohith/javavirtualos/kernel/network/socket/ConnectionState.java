package com.rohith.javavirtualos.kernel.network.socket;

public enum ConnectionState {
    LISTEN,
    SYN_SENT,
    SYN_RECEIVED,
    ESTABLISHED,
    FIN_WAIT,
    CLOSED
}
