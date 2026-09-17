package controller.event.events;

import model.entity.units.Unit;

public class TurnEndEvent {
    public Unit unit;
    public int current_round;

    public TurnEndEvent(Unit target, int current_round) {
        this.unit = target;
        this.current_round = current_round;
    }
}
