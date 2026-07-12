package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;

public class Infusion_Shard implements CatalystEffect {
    String catalyst_name = "Infusion Shard";

    @Override
    public boolean canApply(CraftedEquipment equipment) {
        return true;
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        Crafter.addOneRandomMod(equipment);

        return "เพิ่มม็อดให้กับ "+equipment.getName()+" แล้ว";
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
