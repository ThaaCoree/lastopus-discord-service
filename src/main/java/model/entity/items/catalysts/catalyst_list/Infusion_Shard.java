package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;

public class Infusion_Shard implements CatalystEffect {
    String catalyst_name = "Infusion Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        ModInstance mod = Crafter.randomOneAvailableMod(equipment);
        if (mod == null) return new ValidationResult(false, "ไม่พบม็อดที่เพิ่มได้แล้ว");
        return new ValidationResult(true, "");
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
