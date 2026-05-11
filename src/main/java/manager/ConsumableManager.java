package main.java.manager;

import model.entity.*;
import model.entity.items.Consumable;
import model.entity.units.Unit;
import model.type.ResourceType;

import java.util.Map;

public class ConsumableManager {

    public void consumableUse(Consumable consumable, Unit unit) {
        Map<ResourceType, ResourceData> unitResource = unit.getResources();

        for (Map.Entry<ResourceType, Double> restore : consumable.getRestoredFlat().entrySet()) {
            ResourceType restoreType = restore.getKey();
                unitResource.get(restoreType).sumRemaining(restore.getValue());
        }

        for (Map.Entry<ResourceType, Double> restore : consumable.getRestoredPercent().entrySet()) {
            ResourceType restoreType = restore.getKey();
            double usable = unit.getResources().get(restoreType).getUsable();
                unitResource.get(restoreType).sumRemaining(restore.getValue()*usable);
        }

        for (Map.Entry<ResourceType, Double> restore : consumable.getRestoredMissingPercent().entrySet()) {
            ResourceType restoreType = restore.getKey();
            double remaining = unitResource.get(restoreType).getRemaining();
            double usable = unitResource.get(restoreType).getUsable();
            double missing = usable - remaining;
            unitResource.get(restoreType).sumRemaining(missing);
        }

        for (TimedCondition timedCon : consumable.getConditionsGiven()) {
            Conditions condition = timedCon.getCondition();
            int duration = (int) timedCon.getDuration();
            ConditionManager.applyCondition(condition, unit, duration);
        }
    }
}
