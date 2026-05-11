package manager;

import main.controller.CombatFlow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.units.Unit;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConditionManager {

    public static void applyCondition(Conditions condition, Unit source, Unit target, int duration) {
        ConditionInstance instance = new ConditionInstance(condition, source, duration);
        int nextFreeKey = 1;
        while (target.getConditionInstances().containsKey(nextFreeKey)) {
            nextFreeKey++;
        }
        target.getConditionInstances().put(nextFreeKey, instance);
        target.calculateEverything();
    }

    public static void applyCondition(Conditions condition, Unit target, int duration) {
        ConditionInstance instance = new ConditionInstance(condition, duration);
        int nextFreeKey = 1;
        while (target.getConditionInstances().containsKey(nextFreeKey)) {
            nextFreeKey++;
        }
        target.getConditionInstances().put(nextFreeKey, instance);
        target.calculateEverything();
    }

    public static void reapplyCondition(Conditions condition, Unit target) {
        if (target == null) return;
        for (ConditionInstance ci : target.getConditionInstances().values()) {
            if (ci.getCondition() == null) continue;
            if (ci.getCondition().getName().equals(condition.getName()))
                ci.setCondition(condition);
        }
        target.calculateEverything();
    }

    public static void removeCondition(Unit target, String condition) {
        target.getConditionInstances()
                .entrySet()
                .removeIf(entry -> entry.getValue().getCondition().getName().equals(condition));
        target.calculateEverything();
    }

    public static void removeOneCondition(Unit target, String condition) {
        for (Iterator<Map.Entry<Integer, ConditionInstance>> it = target.getConditionInstances().entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, ConditionInstance> entry = it.next();

            if (entry.getValue().getCondition().getName().equals(condition)) {
                it.remove();
                break;
            }
        }
        target.calculateEverything();
    }

    public static void applyCondition(Conditions condition, Unit source, Unit target, int duration, Map<String, Double> numberRecord) {
        ConditionInstance instance = new ConditionInstance(condition, source, duration);
        numberRecord.forEach(instance::putNumberRecord);
        int nextFreeKey = 1;
        while (target.getConditionInstances().containsKey(nextFreeKey)) {
            nextFreeKey++;
        }
        target.getConditionInstances().put(nextFreeKey, instance);
        target.calculateEverything();
    }

    public static void update(CombatFlow combatFlow) {
        for (Unit unit : combatFlow.getAllUnit().values()) {
            unit.getConditionInstances()
                    .entrySet()
                    .removeIf(entry -> entry.getValue().getDuration() <= entry.getValue().getAppliedTime());
            unit.calculateEverything();
        }
    }

    public static void turnTick(CombatFlow combatFlow) {
        for (Unit unit : combatFlow.getAllUnit().values()) {
            for (Map.Entry<Integer, ConditionInstance> entry : unit.getConditionInstances().entrySet()) {
                ConditionInstance instance = entry.getValue();
                instance.sumAppliedTime(-1);
            }
        }
        update(combatFlow);
    }
}
