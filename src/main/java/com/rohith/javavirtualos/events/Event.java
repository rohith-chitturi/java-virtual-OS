package com.rohith.javavirtualos.events;

public class Event {
    private final String topic;
    private final Object payload;

    public Event(String topic, Object payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public Object getPayload() {
        return payload;
    }
}
