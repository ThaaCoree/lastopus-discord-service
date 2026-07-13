package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import util.WeightedRandom;

public class Unstable_Shard implements CatalystEffect {
    String catalyst_name = "Unstable Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        Crafter.craft(equipment);


        return "คราฟท์ "+equipment.getName()+" ใหม่แล้ว";
    }

    @Override
    public boolean brick(CraftedEquipment equipment) {
        WeightedRandom<Boolean> random = new WeightedRandom<>();
        random.add(true, 5);
        random.add(false, 95);
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
