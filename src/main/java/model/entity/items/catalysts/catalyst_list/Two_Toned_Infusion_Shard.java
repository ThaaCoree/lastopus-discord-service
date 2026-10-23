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
        StringBuilder sb = new StringBuilder();
        ModInstance removed = Crafter.removeOneRandomMod(equipment, sb);
        if (removed == null) {
            return sb.toString();
        }

        ModInstance added = Crafter.addOneRandomMod(equipment, sb);
        if (added == null) {
            return sb.append("ไม่มีม็อดให้เพิ่ม").toString();
        }

        int tier = added.getTier();
        ModifierType modifierType = added.getMod().getModifierType();
        boolean two_handed = equipment.getWeaponType().twoHanded();
        if (ThreadLocalRandom.current().nextBoolean()) {
            added.setBase_rolled(added.getMod().getMaxModValueByTier(tier, equipment.getEquipmentType(), two_handed, modifierType));
            sb.append("ม็อดถูกเปลี่ยนรีโรลให้กลายเป็นตัวเลขสูงสุด");
        } else {
            added.setBase_rolled(added.getMod().getMinModValueByTier(tier, equipment.getEquipmentType(), two_handed, modifierType));
            sb.append("ม็อดถูกเปลี่ยนรีโรลให้กลายเป็นตัวเลขต่ำสุด");
        }
        added.resetFinalValue(equipment.getMaterialInstances(), added);

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
