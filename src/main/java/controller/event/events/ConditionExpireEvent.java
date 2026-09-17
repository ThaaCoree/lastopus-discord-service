package controller.event.events;

import model.entity.Conditions;
import model.entity.units.Unit;

public class ConditionExpireEvent {
    public Conditions condition;
    public int current_round;
    public Unit target;
    public Unit unit_source;
    public String event_source;

    public ConditionExpireEvent(Conditions condition, int current_round, Unit unit_source, Unit target, String event_source) {
        this.condition = condition;
        this.current_round = current_round;
        this.target = target;
        this.unit_source = unit_source;
        this.event_source = event_source;
    }
}
