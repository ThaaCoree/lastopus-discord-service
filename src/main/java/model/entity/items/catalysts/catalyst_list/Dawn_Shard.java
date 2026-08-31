package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.*;
import model.type.EquipmentType;
import model.type.MaterialRole;
import model.type.WeaponType;
import util.WeightedRandom;

import java.util.List;
import java.util.Map;

public class Dawn_Shard implements CatalystEffect {
    String catalyst_name = "Dawn Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment craftedEquipment) {
        craftedEquipment.modInstances.clear();
        craftedEquipment.mod_id = 0;
        EquipmentType equipmentType = craftedEquipment.getEquipmentType();
        WeaponType weaponType = craftedEquipment.getWeaponType();

        //loop in each material
        for (MaterialInstance materialInstance : craftedEquipment.materialInstances) {
            if (materialInstance.material_role == MaterialRole.BOOST) continue;
            //this unique part will be moved later
            boolean horn_light_dragon = false;
            for (MaterialInstance instance : craftedEquipment.materialInstances) {
                if (instance.material.getName().equals("Horn of Light Dragon")) {
                    horn_light_dragon = true;
                }
            }
            WeightedRandom<ModInstance> randomised_mods = new WeightedRandom<>();

            //loop in each pool for a material
            for (Map.Entry<String, CraftingMaterial.CraftPoolEntry> mapEntry : materialInstance.material.pools.entrySet()) {
                String pool = mapEntry.getKey();
                CraftingMaterial.CraftPoolEntry entry = mapEntry.getValue();
                List<MaterialInstance> materialInstances = craftedEquipment.getMaterialInstances();
                //one pool can have many allowed mods
                WeightedRandom<CraftedMod> mod_pool_for_a_material = new WeightedRandom<>();
                //loop in each mod type for a material to put in weightedRandom
                Crafter.putModsIntoWeightedRandom(mod_pool_for_a_material, entry.pool, equipmentType, weaponType);
                Crafter.applyWeightBoostByMaterial(mod_pool_for_a_material, craftedEquipment.getMaterialInstances());

                for (int i = 0; i < entry.maxModsAllowed; i++) {

                    if (mod_pool_for_a_material.isEmpty()) break;
                    //create modifier object to add into craftedEquipment's list

                    ModInstance modInstance = new ModInstance(craftedEquipment.mod_id++,
                            materialInstance.material_id,
                            entry.getPool().getPool_name());

                    UniqueMaterialManager.applyUniqueBoost(mod_pool_for_a_material, craftedEquipment.getMaterialInstances(), modInstance);

                    CraftedMod random = mod_pool_for_a_material.roll();
                    modInstance.setMod(random);
                    double weight = mod_pool_for_a_material.getWeight(random);
                    int maxTier = entry.maxTier;
                    if (horn_light_dragon) {
                        maxTier += 3;
                    }

                    modInstance.setTier(maxTier);
                    modInstance.rerollBaseAndFinal(craftedEquipment.getEquipmentType(), craftedEquipment.getWeaponType().twoHanded());
                    modInstance.resetFinalValue(materialInstances, modInstance);

                    randomised_mods.add(modInstance, weight);
                    //remove if can't duplicate
                    if (!entry.pool.can_duplicate_mod) {
                        mod_pool_for_a_material.remove(random);
                    }
                }

                //create fixed mods
                if (equipmentType == EquipmentType.WEAPON && weaponType != WeaponType.SHIELD && weaponType != WeaponType.CHAIN) {
                    for (CraftedMod fixedMod : entry.pool.weaponMods) {
                        if (!fixedMod.isFixed()) continue;
                        ModInstance modInstance_fixed = new ModInstance(craftedEquipment.mod_id, materialInstance.material_id, entry.getPool().getPool_name(), fixedMod);
                        int maxTier = entry.maxTier;
                        if (horn_light_dragon) {
                            maxTier += 3;
                        }

                        modInstance_fixed.setTier(maxTier);
                        modInstance_fixed.rerollBaseAndFinal(craftedEquipment.getEquipmentType(), craftedEquipment.getWeaponType().twoHanded());
                        modInstance_fixed.resetFinalValue(materialInstances, modInstance_fixed);
                        craftedEquipment.modInstances.add(modInstance_fixed);
                    }
                } else if (equipmentType == EquipmentType.ACCESSORY) {
                    for (CraftedMod fixedMod : entry.pool.accessoryMods) {
                        if (!fixedMod.isFixed()) continue;
                        ModInstance modInstance_fixed = new ModInstance(craftedEquipment.mod_id, materialInstance.material_id, entry.getPool().getPool_name(), fixedMod);
                        int maxTier = entry.maxTier;
                        if (horn_light_dragon) {
                            maxTier += 3;
                        }

                        modInstance_fixed.setTier(maxTier);
                        modInstance_fixed.rerollBaseAndFinal(craftedEquipment.getEquipmentType(), craftedEquipment.getWeaponType().twoHanded());
                        modInstance_fixed.resetFinalValue(materialInstances, modInstance_fixed);
                        craftedEquipment.modInstances.add(modInstance_fixed);
                    }
                } else if (equipmentType == EquipmentType.HELMET || equipmentType == EquipmentType.ARMOR ||
                        equipmentType == EquipmentType.BOOTS || equipmentType == EquipmentType.GLOVES ||
                        weaponType == WeaponType.SHIELD || weaponType == WeaponType.CHAIN) {
                    for (CraftedMod fixedMod : entry.pool.armorMods) {
                        if (!fixedMod.isFixed()) continue;
                        ModInstance modInstance_fixed = new ModInstance(craftedEquipment.mod_id, materialInstance.material_id, entry.getPool().getPool_name(), fixedMod);
                        int maxTier = entry.maxTier;
                        if (horn_light_dragon) {
                            maxTier += 3;
                        }

                        modInstance_fixed.setTier(maxTier);
                        modInstance_fixed.rerollBaseAndFinal(craftedEquipment.getEquipmentType(), craftedEquipment.getWeaponType().twoHanded());
                        modInstance_fixed.resetFinalValue(materialInstances, modInstance_fixed);
                        craftedEquipment.modInstances.add(modInstance_fixed);
                    }
                }
            }
            for (int i = 0; i < materialInstance.material.getMaterial_max_mod(); i++) {
                if (randomised_mods.isEmpty()) break;
                ModInstance modInstance = randomised_mods.roll();
                craftedEquipment.modInstances.add(modInstance);
                randomised_mods.remove(modInstance);
            }
        }
        Crafter.updateModsInCraftedEquipment(craftedEquipment);

        return "คราฟท์ "+craftedEquipment.getName()+" ใหม่โดยม็อดทั้งหมดเป็นเทียร์สูงสุดแล้ว";
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
