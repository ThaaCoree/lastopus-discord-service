package model.entity.items.crafted_equipments;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.CatalystFactory;
import model.entity.items.catalysts.ValidationResult;
import model.type.*;
import util.StatTranslateUtil;
import util.WeightedRandom;

import java.util.*;

public class Crafter {

    public static void craft(CraftedEquipment craftedEquipment) {
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
                //one pool can have many allowed mods
                WeightedRandom<CraftedMod> mod_pool_for_a_material = new WeightedRandom<>();
                UniqueMaterialManager uniqueMaterialManager = new UniqueMaterialManager(mod_pool_for_a_material, craftedEquipment.getMaterialInstances());
                //loop in each mod type for a material to put in weightedRandom
                putModsIntoWeightedRandom(mod_pool_for_a_material, entry.pool, equipmentType, weaponType);

                applyWeightBoostByMaterial(mod_pool_for_a_material, craftedEquipment.getMaterialInstances());
                uniqueMaterialManager.applyUniqueBoost();

                for (int i = 0; i < entry.maxModsAllowed; i++) {
                    if (mod_pool_for_a_material.isEmpty()) break;
                    //create modifier object to add into craftedEquipment's list
                    CraftedMod random = mod_pool_for_a_material.roll();
                    double weight = mod_pool_for_a_material.getWeight(random);
                    randomised_mods.add(evaluateMod(craftedEquipment, horn_light_dragon, materialInstance, entry, entry.pool, random, uniqueMaterialManager), weight);
                    //remove if can't duplicate
                    if (!entry.pool.can_duplicate_mod) {
                        mod_pool_for_a_material.remove(random);
                    }
                }

                //create fixed mods
                if (equipmentType == EquipmentType.WEAPON && weaponType != WeaponType.SHIELD && weaponType != WeaponType.CHAIN) {
                    for (CraftedMod fixedMod : entry.pool.weaponMods) {
                        if (!fixedMod.isFixed()) continue;
                        craftedEquipment.modInstances.add(evaluateMod(craftedEquipment, horn_light_dragon, materialInstance, entry, entry.pool, fixedMod, uniqueMaterialManager));
                    }
                } else if (equipmentType == EquipmentType.ACCESSORY) {
                    for (CraftedMod fixedMod : entry.pool.accessoryMods) {
                        if (!fixedMod.isFixed()) continue;
                        craftedEquipment.modInstances.add(evaluateMod(craftedEquipment, horn_light_dragon, materialInstance, entry, entry.pool, fixedMod, uniqueMaterialManager));
                    }
                } else if (equipmentType == EquipmentType.HELMET || equipmentType == EquipmentType.ARMOR ||
                        equipmentType == EquipmentType.BOOTS || equipmentType == EquipmentType.GLOVES ||
                        weaponType == WeaponType.SHIELD || weaponType == WeaponType.CHAIN) {
                    for (CraftedMod fixedMod : entry.pool.armorMods) {
                        if (!fixedMod.isFixed()) continue;
                        craftedEquipment.modInstances.add(evaluateMod(craftedEquipment, horn_light_dragon, materialInstance, entry, entry.pool, fixedMod, uniqueMaterialManager));
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
        updateModsInCraftedEquipment(craftedEquipment);
    }

    public static void updateModsInCraftedEquipment(CraftedEquipment craftedEquipment) {
        //loop in each mod instance to put real mod into equipment
        //before loop, clear old mods first
        craftedEquipment.getModifiers().getStatModifiers().clear();
        craftedEquipment.getModifiers().getStatusModifiers().clear();
        for (ModInstance modInstance : craftedEquipment.modInstances) {
            CraftedMod mod = modInstance.mod;
            if (mod.isStatMod()) {
                if (mod.modifierType == ModifierType.FLAT) {
                    craftedEquipment.getModifiers().getStatModifierSafe(modInstance.mod.statType).sumFlat(modInstance.final_value);
                }
                if (mod.modifierType == ModifierType.GLOBAL) {
                    craftedEquipment.getModifiers().getStatModifierSafe(modInstance.mod.statType).sumGlobalMult(modInstance.final_value);
                }
                if (mod.modifierType == ModifierType.EQUIPMENT) {
                    craftedEquipment.getModifiers().getStatModifierSafe(modInstance.mod.statType).sumEquipmentMult(modInstance.final_value);
                }
                if (mod.modifierType == ModifierType.OVERRIDE) {
                    craftedEquipment.getModifiers().getStatModifierSafe(modInstance.mod.statType).setOverride(modInstance.final_value);
                }
            }
            if (mod.isStatusMod()) {
                if (mod.modifierType == ModifierType.FLAT) {
                    craftedEquipment.getModifiers().getStatusModifierSafe(modInstance.mod.statusType).sumFlat(modInstance.final_value);
                }
                if (mod.modifierType == ModifierType.GLOBAL) {
                    craftedEquipment.getModifiers().getStatusModifierSafe(modInstance.mod.statusType).sumGlobalMult(modInstance.final_value);
                }
                if (mod.modifierType == ModifierType.EQUIPMENT) {
                    craftedEquipment.getModifiers().getStatusModifierSafe(modInstance.mod.statusType).sumEquipmentMult(modInstance.final_value);
                }
                if (mod.modifierType == ModifierType.OVERRIDE) {
                    craftedEquipment.getModifiers().getStatusModifierSafe(modInstance.mod.statusType).setOverride(modInstance.final_value);
                }
            }
        }

        craftedEquipment.setStatusDescription(StatTranslateUtil.translateStatusDesc(craftedEquipment.getModifiers(), craftedEquipment.getSkills()));
    }

    public static ModInstance evaluateMod(CraftedEquipment craftedEquipment, boolean horn_light_dragon,
                                                           MaterialInstance materialInstance, CraftingMaterial.CraftPoolEntry entry,
                                                           CraftModPool pool, CraftedMod mod,
                                                           UniqueMaterialManager uniqueMaterialManager) {
        ModInstance modifier = (new ModInstance(craftedEquipment.mod_id++,
                materialInstance.material_id,
                pool.pool_name,
                mod));
        int minTier = entry.minTier;
        int maxTier = entry.maxTier;
        if (horn_light_dragon) {
            minTier += 3;
            maxTier += 3;
        }

        modifier.randomizeTierAndBaseValue(minTier, maxTier, craftedEquipment.getEquipmentType(), craftedEquipment.getWeaponType().twoHanded());
        modifier.resetFinalValue();
        uniqueMaterialManager.applyUniqueMultitude();

        return modifier;
    }

    public static boolean shatterItem(CraftedEquipment equipment) {
        int base_materials = 0;
        int boost_materials = 0;
        for (MaterialInstance materialInstance : equipment.getMaterialInstances()) {
            if (materialInstance.material_role == MaterialRole.BASE) {
                base_materials++;
            } else if (materialInstance.material_role == MaterialRole.BOOST) {
                boost_materials++;
            }
        }

        WeightedRandom<Boolean> random = new WeightedRandom<>();
        int base_material_weight = base_materials*100;
        double boost_material_weight = Math.pow(boost_materials, 3)*5;
        if (base_material_weight + boost_material_weight > 300) {
            random.add(false, 100);
            random.add(true, base_material_weight + boost_material_weight);
            return random.roll();
        } else {
            return false;
        }
    }

    public static void putModsIntoWeightedRandom(WeightedRandom<CraftedMod> weightedRandom, CraftModPool pool, EquipmentType equipmentType, WeaponType weaponType) {
        for (CraftedMod mod : pool.getListByEquipType(equipmentType, weaponType)) {
            weightedRandom.add(mod, mod.weight);
        }
    }

    public static void applyWeightBoostByMaterial(WeightedRandom<CraftedMod> weightedRandom, List<MaterialInstance> materialInstances) {
        for (MaterialInstance materialInstance : materialInstances) {
            if (materialInstance.material_role == MaterialRole.BASE) continue;

            materialInstance.material.modBoost.forEach((tag, multiply) -> {
                applyWeightBoostByTag(weightedRandom, tag, multiply);
            });
        }
    }

    public static void applyWeightBoostByTag(WeightedRandom<CraftedMod> weightedRandom, StatTag tag, double multiply) {
        for (CraftedMod mod : new ArrayList<>(weightedRandom.getList().keySet())) {
            if (mod.isStatMod() && mod.statType.hasTag(tag)) {
                weightedRandom.multiplyWeight(mod, multiply);
            }

            if (mod.isStatusMod() && mod.statusType.hasTag(tag)) {
                weightedRandom.multiplyWeight(mod, multiply);
            }
        }
    }

    public static void addOneRandomMod(CraftedEquipment equipment) {
        if (randomOneAvailableMod(equipment) == null) return;
        equipment.getModInstances().add(randomOneAvailableMod(equipment));
        updateModsInCraftedEquipment(equipment);
    }

    public static void removeOneRandomMod(CraftedEquipment equipment) {
        List<ModInstance> modInstances = equipment.getModInstances();

        // หา index ของ mod ที่ไม่ใช่ fixed เท่านั้น
        List<Integer> removableIndices = new ArrayList<>();
        for (int i = 0; i < modInstances.size(); i++) {
            if (!modInstances.get(i).mod.fixed) {
                removableIndices.add(i);
            }
        }

        if (removableIndices.isEmpty()) {
            // ทุกตัวเป็น fixed mod ทั้งหมด -> ไม่มีอะไรให้ลบ หยุดเลย
            return;
        }

        Random random = new Random();
        int pickedIndex = removableIndices.get(random.nextInt(removableIndices.size()));
        modInstances.remove(pickedIndex);

        updateModsInCraftedEquipment(equipment);
    }

    public static void putInOneMod(CraftedEquipment equipment, ModInstance modInstance) {
        equipment.getModInstances().add(modInstance);
        updateModsInCraftedEquipment(equipment);
    }

    public static ModInstance randomOneAvailableMod(CraftedEquipment equipment) {
        WeightedRandom<ModInstance> random = new WeightedRandom<>();
        for (MaterialInstance materialInstance : equipment.materialInstances) {
            if (materialInstance.material_role == MaterialRole.BOOST) continue;
            if (randomAvailableModFromMaterial(equipment.modInstances, materialInstance,
                    equipment, equipment.mod_id++).isEmpty()) continue;
            random.add(randomAvailableModFromMaterial(equipment.modInstances, materialInstance,
                    equipment, equipment.mod_id++).roll(), 100);
        }

        if (random.isEmpty()) {
            return null;
        } else {
            return random.roll();
        }
    }

    public static WeightedRandom<ModInstance> randomAvailableModFromMaterial(List<ModInstance> modInstances, MaterialInstance material,
                                                                              CraftedEquipment equipment, int mod_id) {
        EquipmentType equipmentType = equipment.getEquipmentType();
        WeaponType weaponType = equipment.getWeaponType();
        WeightedRandom<ModInstance> random = new WeightedRandom<>();
        int current_mods = 0;
        List<ModInstance> matched_mods = new ArrayList<>();
        for (ModInstance modInstance : modInstances) {
            if (modInstance.getBase_material_id() == material.getMaterial_id()) {
                matched_mods.add(modInstance);
                current_mods++;
            }
        }
        int material_max_mods = material.getMaterial().getMaterial_max_mod();
        if (current_mods >= material_max_mods) {
            return random;
        }

        for (Map.Entry<String, CraftingMaterial.CraftPoolEntry> entryMap : material.getMaterial().getPools().entrySet()) {
            int max_mods_in_pool = entryMap.getValue().getMaxModsAllowed();
            int matching_mods = 0;

            for (ModInstance modInstance : matched_mods) {
                if (modInstance.getPool_name().equals(entryMap.getValue().getPool().getPool_name())) {
                    matching_mods++;
                }
            }

            if (matching_mods < max_mods_in_pool) {
                CraftModPool pool = entryMap.getValue().getPool();
                List<CraftedMod> candidates = pool.getListByEquipType(equipmentType, weaponType);
                if (candidates == null) candidates = Collections.emptyList();

                if (!pool.can_duplicate_mod) {
                    // ดึงเฉพาะ modInstance ที่อยู่ใน pool นี้มาก่อน
                    List<ModInstance> sameePoolInstances = matched_mods.stream()
                            .filter(mi -> mi.getPool_name().equals(pool.getPool_name()))
                            .toList();

                    for (CraftedMod mod : candidates) {
                        boolean conflicts = sameePoolInstances.stream().anyMatch(mi -> {
                            if (mod.isStatMod() && mi.getMod().isStatMod()) {
                                return mi.getMod().getStatType() == mod.getStatType();
                            }
                            if (mod.isStatusMod() && mi.getMod().isStatusMod()) {
                                return mi.getMod().getStatusType() == mod.getStatusType();
                            }
                            return false;
                        });

                        if (!conflicts) {
                            ModInstance modInstance = new ModInstance(mod_id,material.material_id, pool.getPool_name(), mod);
                            modInstance.randomizeTierAndBaseValue(entryMap.getValue().minTier, entryMap.getValue().maxTier, equipmentType, weaponType.twoHanded());
                            modInstance.resetFinalValue();
                            random.add(modInstance, mod.weight); // add แค่ครั้งเดียวต่อ mod แน่นอน
                        }
                    }
                } else {
                    for (CraftedMod mod : candidates) {
                        ModInstance modInstance = new ModInstance(mod_id, material.material_id, pool.getPool_name(), mod);
                        modInstance.randomizeTierAndBaseValue(entryMap.getValue().minTier, entryMap.getValue().maxTier, equipmentType, weaponType.twoHanded());
                        modInstance.resetFinalValue();
                        random.add(modInstance, mod.weight);
                    }
                }
            }
        }

        return random;
    }

    public static ValidationResult checkCatalyst(String catalyst_name, CraftedEquipment equipment) {
        CatalystFactory catalystFactory = new CatalystFactory();
        CatalystEffect effect = catalystFactory.get(catalyst_name);
        return effect.canApply(equipment);
    }

    public static void useCatalyst(String catalyst_name, CraftedEquipment equipment) {
        CatalystFactory catalystFactory = new CatalystFactory();
        CatalystEffect effect = catalystFactory.get(catalyst_name);
        if (effect == null) return;
        if (effect.canApply(equipment).isSuccess()) {
            effect.apply(equipment);
            equipment.getCatalystInstances().add(new CatalystInstance(equipment.getNext_catalyst_idAndIncrement(), catalyst_name));
            Crafter.updateModsInCraftedEquipment(equipment);
        } else {
            System.out.println("Catalyst ดังกล่าวใช้งานกับไอเทมนี้ไม่ได้\n" +
                    effect.canApply(equipment).getMessage());
        }
    }
}
