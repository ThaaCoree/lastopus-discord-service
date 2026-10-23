package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import util.WeightedRandom;

import java.util.Set;

public class Night_Shard implements CatalystEffect {
    String catalyst_name = "Night Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        Set<ModInstance> lockeds = Crafter.snapshotLockedMods(equipment.getModInstances());
        StringBuilder sb = new StringBuilder();

        Crafter.craft(equipment, sb);
        for (ModInstance modInstance : equipment.getModInstances()) {
            if (lockeds.contains(modInstance)) continue;
            modInstance.multiplyTier(1.5);
            modInstance.rerollBaseAndFinal(equipment.getEquipmentType(), equipment.getWeaponType().twoHanded());
            modInstance.resetFinalValue(equipment.getMaterialInstances(), modInstance);
        }

        return sb.toString()+"คราฟท์ "+equipment.getName()+" โดยม็อดที่เพิ่มมาใหม่มี Tier เพิ่มขึ้นเป็น 1.5 เท่าแล้ว";
    }

    @Override
    public boolean brick(CraftedEquipment equipment) {
        WeightedRandom<Boolean> random = new WeightedRandom<>();
        random.add(true, 2);
        random.add(false, 1);
        if (random.roll()) {
            return Crafter.forceShatterItem(equipment);
        } else {
            return false;
        }
    }

    @Override
    public String getCatalyst_name() {
        return catalyst_name;
    }

}
