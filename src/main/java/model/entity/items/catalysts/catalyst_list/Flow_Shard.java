package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;

public class Flow_Shard implements CatalystEffect {
    String catalyst_name = "Flow Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        Crafter.removeOneRandomMod(equipment);

        return "ลบม็อดออกจาก "+equipment.getName()+" แล้ว";
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
