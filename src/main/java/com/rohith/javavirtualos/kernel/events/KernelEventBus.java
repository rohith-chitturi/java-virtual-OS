package com.rohith.javavirtualos.kernel.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class KernelEventBus {
    private final Map<Class<? extends KernelEvent>, List<Consumer<? extends KernelEvent>>> listeners = new HashMap<>();

    public <T extends KernelEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends KernelEvent> void publish(T event) {
        List<Consumer<? extends KernelEvent>> topicListeners = listeners.get(event.getClass());
        if (topicListeners != null) {
            for (Consumer<? extends KernelEvent> listener : topicListeners) {
                ((Consumer<T>) listener).accept(event);
            }
        }
    }
}
