package manager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.entity.ModifierBundle;
import model.entity.UniqueModifier;
import model.entity.units.Unit;
import model.type.SkillType;
import model.type.StatType;
import model.type.StatusType;
import model.type.UniqueType;

public class UniqueManager {
    @JsonIgnore
    private final Unit unit;

    public UniqueManager(Unit unit) {
        this.unit = unit;
    }

    public void calculateUnique() {
        resetUnique();
        calculateHumanStatuses();
        calculateFairy();
        calculateOrc();
        calculateElf();
        calculateGrayWolf();
        calculateBull();
        calculateLinkHolder();
        calculateBlueWhale();
        calculateTwilightInfusion();
        calculateTwilightInfusionLuck();
        calculateLightWeight();
        calculateWolf();
        calculateStarsDamnation();
    }

    public void resetUnique() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            modifier.setModifiers(new ModifierBundle());
        }
    }

    public void calculateWolf() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.WINEL_WOLF)) {
                double healthPenalty;
                double manaPenalty;
                if (modifier.isActive()) {
                    healthPenalty = -0.2;
                    manaPenalty = -0.5;
                } else {
                    healthPenalty = 0;
                    manaPenalty = 0;
                }
                modifier.getModifiers().getStatModifierSafe(StatType.HEALTHPOINT).sumGlobalMult(healthPenalty);
                modifier.getModifiers().getStatModifierSafe(StatType.MANAPOINT).sumGlobalMult(manaPenalty);
            }
        }
    }

    public void calculateLightWeight() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.LIGHT_WEIGHT)) {
                double agiBonus;
                if (modifier.isActive()) {
                    agiBonus = 1;
                } else {
                    agiBonus = 0;
                }
                modifier.getModifiers().getStatusModifierSafe(StatusType.AGILITY).sumGlobalMult(agiBonus);
            }
        }
    }

    public void calculateHumanStatuses() {
        if (unit.getRace().getStrongStatus() == null || unit.getRace().getWeakStatus() == null) return;
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() != UniqueType.HUMAN) continue;
            if (!modifier.isActive()) continue;
            modifier.getModifiers().getStatusModifierSafe(unit.getRace().getStrongStatus()).setFlat(unit.getLevel()*2);

            modifier.getModifiers().getStatusModifierSafe(unit.getRace().getWeakStatus()).setOverride(Double.NaN);
            unit.calculateStatAndStatus();
            if (unit.getStatuses().get(unit.getRace().getWeakStatus()).getCurrent() >= unit.getLevel()) {
                modifier.getModifiers().getStatusModifierSafe(unit.getRace().getWeakStatus()).setOverride(unit.getLevel());
            }
        }
    }

    public void calculateFairy() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.FAIRY)) {
                double matkBonus;
                double strPenalty;
                double vitPenalty;
                if (modifier.isActive()) {
                    matkBonus = 0.1;
                    strPenalty = -0.5;
                    vitPenalty = -0.5;
                } else {
                    matkBonus = 0;
                    strPenalty = 0;
                    vitPenalty = 0;
                }
                modifier.getModifiers().getStatusModifierSafe(StatusType.STRENGTH).sumGlobalMult(strPenalty);
                modifier.getModifiers().getStatusModifierSafe(StatusType.VITALITY).sumGlobalMult(vitPenalty);
                modifier.getModifiers().getStatModifierSafe(StatType.MAGICALATTACK).sumGlobalMult(matkBonus);
            }
        }
    }

    public void calculateOrc() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.ORC)) {
                double strBonus;
                double vitBonus;
                double mspdPenalty;
                if (modifier.isActive()) {
                    strBonus = 0.5;
                    vitBonus = 0.5;
                    mspdPenalty = -0.5;
                } else {
                    strBonus = 0;
                    vitBonus = 0;
                    mspdPenalty = 0;
                }
                modifier.getModifiers().getStatusModifierSafe(StatusType.STRENGTH).sumGlobalMult(strBonus);
                modifier.getModifiers().getStatusModifierSafe(StatusType.VITALITY).sumGlobalMult(vitBonus);
                modifier.getModifiers().getStatModifierSafe(StatType.MOVEMENTSPEED).sumGlobalMult(mspdPenalty);
            }
        }
    }

    public void calculateElf() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.YASHA_BULL)) {
                double healthPenalty;
                double manaBonus;
                if (modifier.isActive()) {
                    healthPenalty = -0.2;
                    manaBonus = 0.5;
                } else {
                    healthPenalty = 0;
                    manaBonus = 0;
                }
                modifier.getModifiers().getStatModifierSafe(StatType.HEALTHPOINT).sumGlobalMult(healthPenalty);
                modifier.getModifiers().getStatModifierSafe(StatType.MANAPOINT).sumGlobalMult(manaBonus);
            }
        }
    }

    public void calculateGrayWolf() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.LEDA_GRAYWOLF_BONUS)) {
                double mspdBonus = 0;
                double evasionBonus = 0;
                if (modifier.isActive()) {
                    mspdBonus = 0.61;
                    evasionBonus = 0.47;
                }
                modifier.getModifiers().getStatModifierSafe(StatType.MOVEMENTSPEED).sumGlobalMult(mspdBonus);
                modifier.getModifiers().getStatModifierSafe(StatType.EVASION).sumGlobalMult(evasionBonus);
            }

            if (modifier.getName().equals(UniqueType.LEDA_GRAYWOLF_OVERHEAT)) {
                double mspdPenalty = 0;
                double evasionPenalty = 0;
                double speedPenalty = 0;
                if (modifier.isActive()) {
                    mspdPenalty = -0.2;
                    evasionPenalty = -0.2;
                    speedPenalty = -0.2;
                }
                modifier.getModifiers().getStatModifierSafe(StatType.MOVEMENTSPEED).sumGlobalMult(mspdPenalty);
                modifier.getModifiers().getStatModifierSafe(StatType.EVASION).sumGlobalMult(evasionPenalty);
                modifier.getModifiers().getStatModifierSafe(StatType.SPEED).sumGlobalMult(speedPenalty);
            }
        }
    }

    public void calculateBull() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.YASHA_BULL)) {
                double healthBonus;
                if (modifier.isActive()) {
                    healthBonus = 0.25;
                } else {
                    healthBonus = 0;
                }
                modifier.getModifiers().getStatModifierSafe(StatType.HEALTHPOINT).sumGlobalMult(healthBonus);
            }
        }
    }

    public void calculateLinkHolder() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.ALILUS_LINK_HOLDER)) {
                if (modifier.isActive()) {
                    for (StatType type : StatType.values()) {
                        if (type != StatType.RESERVATION)
                        modifier.getModifiers().getStatModifierSafe(type).sumGlobalMult(-0.5);
                    }
                }
            }
        }
    }

    public void calculateBlueWhale() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.TWELVE_BLUE_WHALE)) {
                double waterBonus;
                double mspdPenalty;
                double evasionPenalty;
                if (modifier.isActive()) {
                    waterBonus = 0.2;
                    mspdPenalty = -0.14;
                    evasionPenalty = -0.14;
                } else {
                    waterBonus = 0;
                    mspdPenalty = 0;
                    evasionPenalty = 0;
                }
                modifier.getModifiers().getSkillModifierSafe(SkillType.WATER).sumMult(waterBonus);
                modifier.getModifiers().getStatModifierSafe(StatType.MOVEMENTSPEED).sumGlobalMult(mspdPenalty);
                modifier.getModifiers().getStatModifierSafe(StatType.EVASION).sumGlobalMult(evasionPenalty);
            }
        }
    }

    public void calculateTwilightInfusion() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.TWILIGHT_INFUSION)) {
                double statusBonus;
                if (modifier.isActive()) {
                    statusBonus = 2;
                } else {
                    statusBonus = 0;
                }
                for (StatusType type : StatusType.values()) {
                    modifier.getModifiers().getStatusModifierSafe(type).sumFlat(statusBonus);
                }
            }
        }
    }

    public void calculateTwilightInfusionLuck() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.TWILIGHT_INFUSION_LUCK)) {
                double statusBonus;
                if (modifier.isActive()) {
                    statusBonus = 5;
                } else {
                    statusBonus = 0;
                }

                modifier.getModifiers().getStatusModifierSafe(StatusType.LUCK).sumFlat(statusBonus);
            }
        }
    }

    public void calculateStarsDamnation() {
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getName() == null) continue;
            if (modifier.getName().equals(UniqueType.STARS_DAMNATION)) {
                double statusBonus;
                if (modifier.isActive()) {
                    statusBonus = -3;
                } else {
                    statusBonus = 0;
                }
                for (StatusType type : StatusType.values()) {
                    modifier.getModifiers().getStatusModifierSafe(type).sumFlat(statusBonus);
                }
            }
        }
    }

}
