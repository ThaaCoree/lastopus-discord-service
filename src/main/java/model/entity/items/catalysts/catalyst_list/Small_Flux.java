package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.crafted_equipments.CraftedEquipment;
import util.WeightedRandom;

public class Small_Flux implements CatalystEffect {
    String catalyst_name = "Small Flux";

    @Override
    public boolean canApply(CraftedEquipment equipment) {
        return true;
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        WeightedRandom<CraftedEquipment.ModInstance> weightedRandom = new WeightedRandom<>();

        for (CraftedEquipment.ModInstance modInstance : equipment.getModInstances()) {
            weightedRandom.add(modInstance, 100);
        }

        weightedRandom.roll().rerollBase(equipment.getEquipmentType(), equipment.getWeaponType().twoHanded());

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
