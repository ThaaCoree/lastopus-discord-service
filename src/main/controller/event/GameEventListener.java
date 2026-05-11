package main.controller.event;

public interface GameEventListener<T> {
    void onEvent(T event);
}