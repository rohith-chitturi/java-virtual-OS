package com.rohith.javavirtualos.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Very basic EventBus for loose coupling.
 */
public class EventBus {
    private final Map<String, List<Consumer<Event>>> listeners = new HashMap<>();

    public void subscribe(String topic, Consumer<Event> listener) {
        listeners.computeIfAbsent(topic, k -> new ArrayList<>()).add(listener);
    }

    public void publish(String topic, Event event) {
        List<Consumer<Event>> topicListeners = listeners.get(topic);
        if (topicListeners != null) {
            for (Consumer<Event> listener : topicListeners) {
                listener.accept(event);
            }
        }
    }
}
