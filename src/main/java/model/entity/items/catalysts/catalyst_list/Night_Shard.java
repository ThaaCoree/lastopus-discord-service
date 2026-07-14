package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import util.WeightedRandom;

public class Night_Shard implements CatalystEffect {
    String catalyst_name = "Night Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        Crafter.craft(equipment);
        for (ModInstance modInstance : equipment.getModInstances()) {
            modInstance.multiplyTier(1.5);
            modInstance.rerollBaseAndFinal(equipment.getEquipmentType(), equipment.getWeaponType().twoHanded());
            modInstance.resetFinalValue(equipment.getMaterialInstances(), modInstance);
        }

        return "เพิ่มเทียร์ของ "+equipment.getName()+" และรีโรลแล้ว";
    }

    @Override
    public boolean brick(CraftedEquipment equipment) {
        WeightedRandom<Boolean> random = new WeightedRandom<>();
        random.add(true, 2);
        random.add(false, 1);
        if (random.roll()) {
            return Crafter.shatterItem(equipment);
        } else {
            return false;
        }
    }

    @Override
    public String getCatalyst_name() {
        return catalyst_name;
    }

}
