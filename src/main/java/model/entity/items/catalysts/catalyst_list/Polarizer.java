package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import util.WeightedRandom;

public class Polarizer implements CatalystEffect {
    String catalyst_name = "Polarizer";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        int mod_count = 0;
        for (ModInstance modInstance : equipment.getModInstances()) {
            mod_count++;
        }
        if (mod_count > 1) {
            return new ValidationResult(true, "");
        } else {
            return new ValidationResult(false, "มีจำนวนม็อดไม่พอ");
        }
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        WeightedRandom<ModInstance> random_mod = new WeightedRandom<>();
        int mod_count = 0;
        for (ModInstance modInstance : equipment.getModInstances()) {
            random_mod.add(modInstance, 10);
            mod_count++;
        }

        mod_count /= 2;
        for (int i = 0; i < mod_count; i++) {
            ModInstance modInstance = random_mod.rollAndRemove();
            if (i < 2) {
                if (modInstance.getFinal_value() > 0) {
                    modInstance.multiplyFinal_value(-1);
                }
            } else {
                modInstance.multiplyFinal_value(-1);
            }
        }

        return "สลับค่าใน "+equipment.getName()+" แล้ว";
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
