package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;
import model.type.ModifierType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Two_Toned_Infusion_Shard implements CatalystEffect {
        String catalyst_name = "Two-Toned Infusion Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        Crafter.removeOneRandomMod(equipment);
        Crafter.addOneRandomMod(equipment);
        List<ModInstance> mods = equipment.getModInstances();
        ModInstance last = mods.get(mods.size() - 1);
        int tier = last.getTier();
        ModifierType modifierType = last.getMod().getModifierType();
        boolean two_handed = equipment.getWeaponType().twoHanded();
        if (ThreadLocalRandom.current().nextBoolean()) {
            last.setBase_rolled(last.getMod().getMaxModValueByTier(tier, equipment.getEquipmentType(), two_handed, modifierType));
        } else {
            last.setBase_rolled(last.getMod().getMinModValueByTier(tier, equipment.getEquipmentType(), two_handed, modifierType));
        }
        last.resetFinalValue(equipment.getMaterialInstances(), last);

        return "ลบม็อดและเพิ่มใหม่ให้กับ "+equipment.getName()+" แล้ว";
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
