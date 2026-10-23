package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import model.type.StatTag;
import util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;

public class Flashing_Shard implements CatalystEffect {
    String catalyst_name = "Flashing Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        for (ModInstance modInstance : equipment.modInstances) {
            if (!modInstance.getMod().getTags().contains(StatTag.STRIKE) && !modInstance.fixed) {
                return new ValidationResult(true, "");
            }
        }
        return new ValidationResult(false, "ไม่มีม็อดที่ลบได้");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        WeightedRandom<ModInstance> strikeMods = new WeightedRandom<>();
        for (ModInstance modInstance : equipment.modInstances) {
            if (!modInstance.getMod().getTags().contains(StatTag.STRIKE) && !modInstance.fixed) {
                strikeMods.add(modInstance, 10);
            }
        }

        ModInstance rolledMod = strikeMods.rollAndRemove();
        if (Crafter.tryAffect(rolledMod)) {
            equipment.modInstances.remove(rolledMod);
            ModInstance candidate = null;
            for (int i = 0; i < 100; i++) {
                candidate = Crafter.randomOneAvailableMod(equipment);
                if (candidate == null) return "ลบ\n"+Crafter.getModText(rolledMod, equipment)+"แล้วแต่ไม่สามารถเพิ่มม็อดได้อีก";
                if (candidate.getMod().getTags().contains(StatTag.STRIKE)) break;
            }
            Crafter.putInOneMod(equipment, candidate);

            return "ลบ\n"+Crafter.getModText(rolledMod, equipment)+"และเพิ่ม\n"+Crafter.getModText(candidate, equipment)+" แล้ว";
        } else {
            ModInstance candidate = null;
            for (int i = 0; i < 100; i++) {
                candidate = Crafter.randomOneAvailableMod(equipment);
                if (candidate == null) return "ม็อดที่จะลบถูกล็อกและไม่สามารถเพิ่มม็อดได้อีก";
                if (candidate.getMod().getTags().contains(StatTag.STRIKE)) break;
            }
            Crafter.putInOneMod(equipment, candidate);

            return "ม็อดที่จะลบถูกล็อกและเพิ่มม็อด\n"+Crafter.getModText(candidate, equipment)+"แล้ว";
        }
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
