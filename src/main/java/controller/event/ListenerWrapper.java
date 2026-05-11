package main.java.controller.event;

class ListenerWrapper<T> {
    GameEventListener<T> listener;
    int priority;

    public ListenerWrapper(GameEventListener<T> listener, int priority) {
        this.listener = listener;
        this.priority = priority;
    }
}