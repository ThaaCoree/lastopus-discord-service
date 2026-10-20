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
        boolean removed = Crafter.removeOneRandomMod(equipment);
        if (!removed) {
            return "ไม่มีม็อดถูกลบ (ม็อดที่ถูกเลือกล็อกอยู่และถูกปลดล็อกแล้ว หรือไม่มีม็อดที่ลบได้)";
        }

        ModInstance added = Crafter.addOneRandomMod(equipment);
        if (added == null) {
            return "ลบม็อดแล้ว แต่ไม่มีม็อดให้เพิ่มใหม่";
        }

        int tier = added.getTier();
        ModifierType modifierType = added.getMod().getModifierType();
        boolean two_handed = equipment.getWeaponType().twoHanded();
        if (ThreadLocalRandom.current().nextBoolean()) {
            added.setBase_rolled(added.getMod().getMaxModValueByTier(tier, equipment.getEquipmentType(), two_handed, modifierType));
        } else {
            added.setBase_rolled(added.getMod().getMinModValueByTier(tier, equipment.getEquipmentType(), two_handed, modifierType));
        }
        added.resetFinalValue(equipment.getMaterialInstances(), added);

        return "ลบม็อดและเพิ่มใหม่ให้กับ " + equipment.getName() + " แล้ว";
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
