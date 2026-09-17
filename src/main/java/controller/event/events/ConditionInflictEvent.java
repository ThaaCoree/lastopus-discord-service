package controller.event.events;

import model.entity.Conditions;
import model.entity.units.Unit;

public class ConditionInflictEvent {
    public Conditions condition;
    public int duration;
    public Unit target;
    public Unit unit_source;
    public String event_source;

    public ConditionInflictEvent(Conditions condition, int duration, Unit unit_source, Unit target, String event_source) {
        this.condition = condition;
        this.duration = duration;
        this.target = target;
        this.unit_source = unit_source;
        this.event_source = event_source;
    }
}
