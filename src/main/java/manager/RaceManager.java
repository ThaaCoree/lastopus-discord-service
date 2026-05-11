package manager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.entity.units.Unit;
import model.type.StatusType;

public class RaceManager {
    @JsonIgnore
    private final Unit unit;

    public RaceManager(Unit unit) {
        this.unit = unit;
    }

    public void setHumanStrongStatus(StatusType type) {
        if (unit.getRace() == null || !unit.getRace().getName().equals("Human")) return;
        unit.getRace().setStrongStatus(type);
    }

    public void setHumanWeakStatus(StatusType type) {
        if (unit.getRace() == null || !unit.getRace().getName().equals("Human")) return;
        unit.getRace().setWeakStatus(type);
    }

    public void setHumanStrongAndWeakStatus(StatusType strong, StatusType weak) {
        if (unit.getRace() == null || !unit.getRace().getName().equals("Human")) return;
        unit.getRace().setStrongStatus(strong);
        unit.getRace().setWeakStatus(weak);
    }
}
