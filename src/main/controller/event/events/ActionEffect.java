package main.controller.event.events;

import model.type.ActionEffectType;

public class ActionEffect {
    public ActionEffectType type;
    public double baseValue;
    public double finalValue;

    public ActionEffect(ActionEffectType type, double baseValue) {
        this.type = type;
        this.baseValue = baseValue;
        this.finalValue = baseValue;
    }
}
