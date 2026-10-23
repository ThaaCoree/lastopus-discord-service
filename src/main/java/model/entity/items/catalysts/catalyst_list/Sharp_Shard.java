package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import model.type.StatTag;
import util.WeightedRandom;

public class Sharp_Shard implements CatalystEffect {
    String catalyst_name = "Sharp Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        for (ModInstance modInstance : equipment.modInstances) {
            if (!modInstance.getMod().getTags().contains(StatTag.CRIT) && !modInstance.fixed) {
                return new ValidationResult(true, "");
            }
        }
        return new ValidationResult(false, "ไม่มีม็อดที่ลบได้");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        WeightedRandom<ModInstance> strikeMods = new WeightedRandom<>();
        StringBuilder sb = new StringBuilder();
        for (ModInstance modInstance : equipment.modInstances) {
            if (!modInstance.getMod().getTags().contains(StatTag.STRIKE) && !modInstance.fixed) {
                strikeMods.add(modInstance, 10);
            }
        }

        ModInstance rolledMod = strikeMods.rollAndRemove();
        if (Crafter.tryAffect(rolledMod, equipment, sb)) {
            Crafter.removeOneSpecificMod(equipment,rolledMod, sb);
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
