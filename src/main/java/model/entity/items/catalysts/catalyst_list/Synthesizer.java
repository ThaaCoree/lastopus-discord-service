package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;

public class Synthesizer implements CatalystEffect {
    String catalyst_name = "Synthesizer";

    @Override
    public boolean canApply(CraftedEquipment equipment) {
        return true;
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        CraftedEquipment.ModInstance mod1 = Crafter.randomOneAvailableMod(equipment);
        CraftedEquipment.ModInstance mod2 = Crafter.randomOneAvailableMod(equipment);


        CraftedEquipment.ModInstance mod_final;
        if (mod1 == null) return "ไม่พบม็อดที่เพิ่มได้แล้ว";
        if (mod2 == null) return "ไม่พบม็อดที่เพิ่มได้แล้ว";
        if (mod1.getTier() > mod2.getTier()) {
            mod_final = mod1;
        } else {
            mod_final = mod2;
        }
        Crafter.putInOneMod(equipment, mod_final);

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
