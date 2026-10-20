package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import util.WeightedRandom;

public class Splintering_Shard implements CatalystEffect {
    String catalyst_name = "Splintering Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        for (ModInstance modInstance : equipment.modInstances) {
            if (modInstance.locked) {
                return new ValidationResult(true, "");
            }
        }
        return new ValidationResult(false, "ไม่มีม็อดที่ถูกล็อกอยู่");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        WeightedRandom<ModInstance> lockedMods = new WeightedRandom<>();
        for (ModInstance modInstance : equipment.modInstances) {
            if (modInstance.locked) {
                lockedMods.add(modInstance, 10);
            }
        }

        ModInstance rolledMod = lockedMods.roll();
        int tier = rolledMod.getTier();
        Crafter.unlockMod(rolledMod);

        WeightedRandom<ModInstance> removingMods = new WeightedRandom<>();
        for (ModInstance modInstance : equipment.modInstances) {
            if (modInstance.fixed) continue;
            if (modInstance.getTier() < tier) {
                removingMods.add(modInstance, 10);
            }
        }

        if (Crafter.removeOneSpecificMod(equipment, removingMods.roll())) {
            return "ลบม็อดออกจาก "+equipment.getName()+" แล้ว";
        } else {
            return "ไม่มีม็อดที่ถูกลบได้ใน "+equipment.getName();
        }
    }

    @Override
    public boolean brick(CraftedEquipment equipment) {
        return false;
    }

    @Override
    public String getCatalyst_name() {
        return catalyst_name;
    }

}
