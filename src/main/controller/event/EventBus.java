package main.controller.event;
import model.type.EventPhase;

import java.util.*;

public class EventBus {
    private Map<EventPhase, Map<Class<?>, List<ListenerWrapper<?>>>> phaseListeners = new HashMap<>();

    public <T> void register(Class<T> eventClass, EventPhase phase, int priority, GameEventListener<T> listener) {

        phaseListeners
                .computeIfAbsent(phase, p -> new HashMap<>())
                .computeIfAbsent(eventClass, c -> new ArrayList<>())
                .add(new ListenerWrapper<>(listener, priority));

        // sort priority (มาก → น้อย)
        phaseListeners.get(phase)
                .get(eventClass)
                .sort((a, b) -> Integer.compare(b.priority, a.priority));
    }

    public <T> void post(T event, EventPhase phase) {
        Class<?> eventClass = event.getClass();

        while (eventClass != null) {
            Map<Class<?>, List<ListenerWrapper<?>>> map = phaseListeners.get(phase);
            if (map != null) {
                List<ListenerWrapper<?>> eventListeners = map.get(eventClass);
                if (eventListeners != null) {
                    // กัน concurrent modification
                    for (ListenerWrapper<?> wrapper : new ArrayList<>(eventListeners)) {

                        @SuppressWarnings("unchecked")
                        GameEventListener<T> listener = (GameEventListener<T>) wrapper.listener;

                        listener.onEvent(event);
                    }
                }
            }
            eventClass = eventClass.getSuperclass();
        }
    }

    public <T> void post(T event) {
        post(event, EventPhase.PRE);
        post(event, EventPhase.MODIFY);
        post(event, EventPhase.POST);
    }

    public Map<EventPhase, Map<Class<?>, List<ListenerWrapper<?>>>> getListeners() {
        return phaseListeners;
    }
}
