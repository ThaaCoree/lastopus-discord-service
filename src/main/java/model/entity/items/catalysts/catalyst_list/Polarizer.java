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
        StringBuilder sb = new StringBuilder();
        int mod_count = 0;
        for (ModInstance modInstance : equipment.getModInstances()) {
            random_mod.add(modInstance, 10);
            mod_count++;
        }

        mod_count /= 2;
        sb.append("ม็อดที่ถูกสลับแล้ว : \n");
        for (int i = 0; i < mod_count; i++) {
            ModInstance modInstance = random_mod.rollAndRemove();
            if (!Crafter.tryAffect(modInstance, equipment, sb)) continue;
            if (i < 2) {
                modInstance.negativeFinal_value();
                sb.append(modInstance.toString(equipment)).append(" (กลายเป็นติดลบจากผลของ Polarizer)");
            } else {
                modInstance.multiplyFinal_value(-1);
                sb.append(modInstance.toString(equipment));
            }
        }

        return sb.toString();
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
