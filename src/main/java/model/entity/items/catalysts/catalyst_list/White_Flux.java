package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;

public class White_Flux implements CatalystEffect {
    String catalyst_name = "White Flux";

    @Override
    public boolean canApply(CraftedEquipment equipment) {
        return true;
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        for (ModInstance modInstance : equipment.getModInstances()) {
            modInstance.rerollBase(equipment.getEquipmentType(), equipment.getWeaponType().twoHanded());
        }

        return "รีโรลค่าใน "+equipment.getName()+" แล้ว";
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
