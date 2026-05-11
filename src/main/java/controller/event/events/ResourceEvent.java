package controller.event.events;

import model.entity.units.Unit;
import model.type.ActionEffectType;

public class ResourceEvent {
    public String event_source;
    public Unit source;
    public Unit target;
    public double amount;
    public ActionEffectType effectType;
    public boolean bypassDebris;

    public ResourceEvent(String event_source, Unit source, Unit target, double amount, ActionEffectType actionEffectType) {
        this.event_source = event_source;
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.effectType = actionEffectType;
        bypassDebris = false;
    }

    public ResourceEvent(String event_source, Unit source, Unit target, double amount, ActionEffectType effectType, boolean bypassDebris) {
        this.event_source = event_source;
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.effectType = effectType;
        this.bypassDebris = bypassDebris;
    }

    public boolean isDamage() {
        return effectType == ActionEffectType.DAMAGE_MAGICAL ||
                effectType == ActionEffectType.DAMAGE_PHYSICAL ||
                effectType == ActionEffectType.DAMAGE_PURE ||
                effectType == ActionEffectType.DAMAGE_TRUE;
    }
}
