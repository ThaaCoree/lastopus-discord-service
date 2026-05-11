package manager;

import model.entity.units.Unit;
import model.type.StatusType;

public class StatusManager {
    private final Unit unit;

    public StatusManager(Unit unit) {
        this.unit = unit;
    }

    public void sumStatusBy(StatusType type, int num) {
        System.out.println("Unit Before: " + unit.getRaisedStatuses().get(type)); // Log ค่าเดิม
        System.out.println("Type: " + type); // Log StatusType
        unit.getRaisedStatuses().merge(type, num, Integer::sum);
        System.out.println("After: " + unit.getRaisedStatuses().get(type)); // Log ค่าใหม่
        unit.recalculateRemainingStatusPoint();
        unit.calculateEverything();
    }

    public void increaseStatusByOne(StatusType type) {
        unit.getRaisedStatuses().merge(type, 1, Integer::sum);
        unit.recalculateRemainingStatusPoint();
        unit.calculateEverything();
    }

    public void decreaseStatusByOne(StatusType type) {
        unit.getRaisedStatuses().merge(type, -1, Integer::sum);
        unit.recalculateRemainingStatusPoint();
        unit.calculateEverything();
    }

}
