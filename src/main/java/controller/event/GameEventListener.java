package main.java.controller.event;

public interface GameEventListener<T> {
    void onEvent(T event);
}