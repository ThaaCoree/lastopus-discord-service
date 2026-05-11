package manager;

import model.entity.ResourceData;
import model.entity.skills.SkillInstance;
import model.entity.units.Unit;
import model.type.ResourceType;
import model.type.StatType;

import java.util.Map;

public class ResourceManager {

    private final Unit unit;

    public ResourceManager(Unit unit) {
        this.unit = unit;
    }

    public void updateMax() {
        updateReservation();
        double oldUsableHealth = 0;
        double oldUsableMana = 0;

        double oldRemainingHealth = 0;
        double oldRemainingMana = 0;
        for (Map.Entry<ResourceType, ResourceData> entry : unit.getResources().entrySet()) {
            if (entry.getKey() == ResourceType.HEALTH) {
                oldUsableHealth = entry.getValue().getUsable();
                oldRemainingHealth = entry.getValue().getRemaining();
            }
            if (entry.getKey() == ResourceType.MANA) {
                oldUsableMana = entry.getValue().getUsable();
                oldRemainingMana = entry.getValue().getRemaining();
            }
        }
        double maxHealth = unit.getStats().get(StatType.HEALTHPOINT).getFinal();
        double maxMana = unit.getStats().get(StatType.MANAPOINT).getFinal();

        double healthReservedPercent = unit.getResources().get(ResourceType.HEALTH).getReservedPercent();
        double healthReservedFlat = unit.getResources().get(ResourceType.HEALTH).getReservedFlat();
        double manaReservedPercent = unit.getResources().get(ResourceType.MANA).getReservedPercent();
        double manaReservedFlat = unit.getResources().get(ResourceType.MANA).getReservedFlat();

        double newUsableHealth = Math.round(((maxHealth * (1 - healthReservedPercent)) - healthReservedFlat) * 100.0) / 100.0;
        double newUsableMana = Math.round(((maxMana * (1 - manaReservedPercent)) - manaReservedFlat) * 100.0) / 100.0;

        unit.getResources().get(ResourceType.HEALTH).setUsable(newUsableHealth);
        unit.getResources().get(ResourceType.MANA).setUsable(newUsableMana);

        double healthChanged = newUsableHealth - oldUsableHealth;
        double manaChanged = newUsableMana - oldUsableMana;

        unit.getResources().get(ResourceType.HEALTH).sumRemaining(healthChanged);
        unit.getResources().get(ResourceType.MANA).sumRemaining(manaChanged);

        double newRemainingHealth = unit.getResources().get(ResourceType.HEALTH).getRemaining();
        double newRemainingMana = unit.getResources().get(ResourceType.MANA).getRemaining();

//        if (healthChanged != 0) {
//            if (healthChanged > 0) {
//                System.out.println("Unit "+unit.getName()+" gained "+healthChanged+" health ("+oldRemainingHealth+" > "+newRemainingHealth+") [MaxHealth changed "+oldUsableHealth+" > "+newUsableHealth+"]");
//            } else if (healthChanged < 0) {
//                System.out.println("Unit "+unit.getName()+" lost "+Math.abs(healthChanged)+" health ("+oldRemainingHealth+" > "+newRemainingHealth+") [MaxHealth changed "+oldUsableHealth+" > "+newUsableHealth+"]");
//            }
//        }
//        if (manaChanged != 0) {
//            if (manaChanged > 0) {
//                System.out.println("Unit "+unit.getName()+" gained "+manaChanged+" mana ("+oldRemainingMana+" > "+newRemainingMana+") [MaxMana changed "+oldUsableMana+" > "+newUsableMana+"]");
//            } else if (manaChanged < 0) {
//                System.out.println("Unit "+unit.getName()+" lost "+Math.abs(manaChanged)+" mana ("+oldRemainingMana+" > "+newRemainingMana+") [MaxMana changed "+oldUsableMana+" > "+newUsableMana+"]");
//            }
//        }
    }

    public void updateReservation() {
        double manaReserveFlat = 0;
        double manaReservePercent = 0;
        double healthReserveFlat = 0;
        double healthReservePercent = 0;

        double reserveEffi = unit.getStats().get(StatType.RESERVATION).getFinal();
        for (SkillInstance instance : unit.getAllSkill().values()) {
            if (instance.isReserving()) {
                manaReservePercent += (instance.getSkillData().getManaReservePercent());
                manaReserveFlat += (instance.getSkillData().getManaReserveFlat());
                healthReservePercent += (instance.getSkillData().getHealthReservePercent());
                healthReserveFlat += (instance.getSkillData().getHealthReserveFlat());
            }
        }
        manaReservePercent *= (reserveEffi);
        manaReserveFlat *= (reserveEffi);
        healthReservePercent *= (reserveEffi);
        healthReserveFlat *= (reserveEffi);

        unit.getResources().get(ResourceType.MANA).setReservedPercent(manaReservePercent);
        unit.getResources().get(ResourceType.MANA).setReservedFlat(manaReserveFlat);
        unit.getResources().get(ResourceType.HEALTH).setReservedPercent(healthReservePercent);
        unit.getResources().get(ResourceType.HEALTH).setReservedFlat(healthReserveFlat);
    }
}
