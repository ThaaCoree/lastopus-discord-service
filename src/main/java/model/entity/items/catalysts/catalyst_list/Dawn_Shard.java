package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.*;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class Dawn_Shard implements CatalystEffect {
    String catalyst_name = "Dawn Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        // จำม็อดล็อกไว้ก่อน เพราะ craft จะปลดล็อกพวกมัน
        // พวกนี้รอดจากการกระทำ จึงไม่ถูกดันเทียร์
        Set<ModInstance> lockeds = Crafter.snapshotLockedMods(equipment.getModInstances());

        Crafter.craft(equipment);

        boolean horn = Crafter.hasHornOfLightDragon(equipment);
        for (ModInstance m : equipment.getModInstances()) {
            if (lockeds.contains(m)) continue;
            setToMaxTier(equipment, m, horn);
        }

        return "คราฟท์ " + equipment.getName() + " ใหม่โดยม็อดทั้งหมดเป็นเทียร์สูงสุดแล้ว";
    }

    private void setToMaxTier(CraftedEquipment equipment, ModInstance mod, boolean horn) {
        CraftingMaterial.CraftPoolEntry entry = findEntry(equipment, mod);
        if (entry == null) return;

        int maxTier = entry.maxTier + (horn ? 3 : 0);
        mod.setTier(maxTier);
        mod.rerollBaseAndFinal(equipment.getEquipmentType(), equipment.getWeaponType().twoHanded());
        mod.resetFinalValue(equipment.getMaterialInstances(), mod);
    }

    private CraftingMaterial.CraftPoolEntry findEntry(CraftedEquipment equipment, ModInstance mod) {
        for (MaterialInstance mi : equipment.getMaterialInstances()) {
            if (mi.getMaterial_id() != mod.getBase_material_id()) continue;
            for (CraftingMaterial.CraftPoolEntry entry : mi.getMaterial().getPools().values()) {
                if (entry.getPool().getPool_name().equals(mod.getPool_name())) {
                    return entry;
                }
            }
        }
        return null;
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