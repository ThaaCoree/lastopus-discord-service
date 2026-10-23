package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import model.type.ModifierType;

import java.text.DecimalFormat;

public class White_Flux implements CatalystEffect {
    String catalyst_name = "White Flux";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        StringBuilder sb = new StringBuilder();
        for (ModInstance modInstance : equipment.getModInstances()) {
            if (!Crafter.tryAffect(modInstance, equipment, sb)) continue;
            sb.append("รีโรลค่าของ\n").append(modInstance.toString(equipment));
            DecimalFormat df = new DecimalFormat("#.##");
            double old_roll = modInstance.final_value;
            if (modInstance.getMod().isStatMod() && modInstance.getMod().getStatType().isPercentage() ||
            modInstance.getMod().modifierTypePercentage()) {
                old_roll*=100;
            }
            modInstance.rerollBaseAndFinal(equipment.getEquipmentType(), equipment.getWeaponType().twoHanded());
            double new_roll = modInstance.final_value;
            if (modInstance.getMod().isStatMod() && modInstance.getMod().getStatType().isPercentage() ||
                    modInstance.getMod().modifierTypePercentage()) {
                new_roll*=100;
            }
            sb.append("[").append(old_roll).append(" -> ").append(new_roll).append("]");
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
