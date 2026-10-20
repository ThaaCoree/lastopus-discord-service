package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import util.WeightedRandom;

public class Stasis_Shard implements CatalystEffect {
    String catalyst_name = "Stasis Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        WeightedRandom<ModInstance> random = new WeightedRandom<>();
        for (ModInstance modInstance : equipment.modInstances) {
            random.add(modInstance, 10);
        }
        random.roll().locked = true;

        return "สุ่มล็อกหนึ่งม็อดใน "+equipment.getName()+" แล้ว";
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
