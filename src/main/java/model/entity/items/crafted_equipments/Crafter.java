package model.entity.items.crafted_equipments;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.CatalystFactory;
import model.entity.items.catalysts.ValidationResult;
import model.type.*;
import util.StatTranslateUtil;
import util.WeightedRandom;

import java.util.*;

public class Crafter {

    public static void craft(CraftedEquipment equipment) {
        // 1. craft กระทำกับทุกม็อด
        //    ตัวไม่ล็อก -> ถูกลบ, ตัวล็อก -> รอด แต่ถูกปลดล็อก
        equipment.modInstances.removeIf(Crafter::tryAffect);

        // 2. id ถัดไปต้องไม่ชนกับตัวที่รอด
        equipment.mod_id = equipment.modInstances.stream()
                .mapToInt(m -> m.mod_id)
                .max().orElse(-1) + 1;

        // 3. เติมม็อดสุ่มทีละวัสดุ
        for (MaterialInstance material : equipment.materialInstances) {
            if (material.material_role == MaterialRole.BOOST) continue;
            fillMaterial(equipment, material);
        }

        // 4. fixed mods
        addFixedMods(equipment);

        updateModsInCraftedEquipment(equipment);
    }

    private static void fillMaterial(CraftedEquipment equipment, MaterialInstance material) {
        while (true) {
            WeightedRandom<ModInstance> candidates = randomAvailableModFromMaterial(
                    equipment.modInstances, material, equipment, equipment.mod_id);
            if (candidates.isEmpty()) break;   // เต็มโควต้า / pool เต็ม / ไม่เหลือตัวเลือก
            equipment.modInstances.add(candidates.roll());
            equipment.mod_id++;
        }
    }

    private static void addFixedMods(CraftedEquipment equipment) {
        boolean horn = hasHornOfLightDragon(equipment);
        for (MaterialInstance mi : equipment.materialInstances) {
            if (mi.material_role == MaterialRole.BOOST) continue;
            for (CraftingMaterial.CraftPoolEntry entry : mi.material.pools.values()) {
                List<CraftedMod> list = entry.pool.getListByEquipType(
                        equipment.getEquipmentType(), equipment.getWeaponType());
                if (list == null) continue;
                for (CraftedMod fixedMod : list) {
                    if (!fixedMod.isFixed()) continue;
                    boolean alreadyHas = equipment.modInstances.stream().anyMatch(m ->
                            m.mod == fixedMod
                                    && m.getBase_material_id() == mi.getMaterial_id()
                                    && m.getPool_name().equals(entry.getPool().getPool_name()));
                    if (alreadyHas) continue;   // กรณี fixed ตัวที่ล็อกไว้
                    ModInstance inst = new ModInstance(equipment.mod_id++, mi.material_id,
                            entry.getPool().getPool_name(), fixedMod);
                    equipment.modInstances.add(
                            evaluateMod(equipment, horn, inst, entry, equipment.materialInstances));
                }
            }
        }
    }

    public static boolean hasHornOfLightDragon(CraftedEquipment equipment) {
        return equipment.materialInstances.stream()
                .anyMatch(i -> i.material.getName().equals("Horn of Light Dragon"));
    }

    public static void unlockAllLockedMods(List<ModInstance> modInstances) {
        for (ModInstance modInstance : modInstances) {
            unlockMod(modInstance);
        }
    }

    public static void unlockMod(ModInstance modInstance) {
        modInstance.locked = false;
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
                if (mod.modifierType == ModifierType.INCREASED) {
                    craftedEquipment.getModifiers().getStatModifierSafe(modInstance.mod.statType).sumMult(modInstance.final_value);
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
                if (mod.modifierType == ModifierType.INCREASED) {
                    craftedEquipment.getModifiers().getStatusModifierSafe(modInstance.mod.statusType).sumMult(modInstance.final_value);
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
                                                           ModInstance modifier, CraftingMaterial.CraftPoolEntry entry, List<MaterialInstance> materialInstances) {

        int minTier = entry.minTier;
        int maxTier = entry.maxTier;
        if (horn_light_dragon) {
            minTier += 3;
            maxTier += 3;
        }

        modifier.randomizeTierAndBaseValue(minTier, maxTier, craftedEquipment.getEquipmentType(), craftedEquipment.getWeaponType().twoHanded());
        modifier.resetFinalValue(materialInstances, modifier);

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
            random.add(false, 200);
            random.add(true, base_material_weight + boost_material_weight);
            return random.roll();
        } else {
            return false;
        }
    }

    public static boolean forceShatterItem(CraftedEquipment equipment) {
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
            random.add(false, 200);
            random.add(true, base_material_weight + boost_material_weight);
            return random.roll();
        } else {
            random.add(false, 8);
            random.add(true, 2);
            return random.roll();
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

    public static void applyWeightBoostByMaterial(WeightedRandom<CraftedMod> weightedRandom, MaterialInstance materialInstance) {
        if (materialInstance.material_role == MaterialRole.BASE) return;

        materialInstance.material.modBoost.forEach((tag, multiply) -> {
            applyWeightBoostByTag(weightedRandom, tag, multiply);
        });
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

    public static ModInstance addOneRandomMod(CraftedEquipment equipment) {
        ModInstance mod = randomOneAvailableMod(equipment);
        if (mod == null) return null;
        equipment.getModInstances().add(mod);
        updateModsInCraftedEquipment(equipment);
        return mod;
    }

    public static boolean removeOneRandomMod(CraftedEquipment equipment) {
        List<ModInstance> modInstances = equipment.getModInstances();

        List<Integer> removableIndices = new ArrayList<>();
        for (int i = 0; i < modInstances.size(); i++) {
            if (!modInstances.get(i).mod.fixed) {      // ม็อดล็อกยังอยู่ในตัวเลือก
                removableIndices.add(i);
            }
        }
        if (removableIndices.isEmpty()) return false;

        int pickedIndex = removableIndices.get(new Random().nextInt(removableIndices.size()));
        boolean removed = false;
        if (tryAffect(modInstances.get(pickedIndex))) {
            modInstances.remove(pickedIndex);
            removed = true;
        }

        updateModsInCraftedEquipment(equipment);
        return removed;
    }

    public static boolean removeOneSpecificMod(CraftedEquipment equipment, ModInstance modInstance) {
        if (Crafter.tryAffect(modInstance)) {
            equipment.modInstances.remove(modInstance);
            return true;
        } else {
            return false;
        }
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

    /** เช็กเฉยๆ ว่ายังเพิ่มม็อดได้อีกไหม ไม่แตะ mod_id และไม่แก้ equipment */
    public static boolean hasAvailableMod(CraftedEquipment equipment) {
        for (MaterialInstance mi : equipment.materialInstances) {
            if (mi.material_role == MaterialRole.BOOST) continue;
            if (!randomAvailableModFromMaterial(
                    equipment.modInstances, mi, equipment, equipment.mod_id).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static WeightedRandom<ModInstance> randomAvailableModFromMaterial(List<ModInstance> modInstances, MaterialInstance material,
                                                                              CraftedEquipment equipment, int mod_id) {
        boolean horn = hasHornOfLightDragon(equipment);
        EquipmentType equipmentType = equipment.getEquipmentType();
        WeaponType weaponType = equipment.getWeaponType();
        WeightedRandom<ModInstance> random = new WeightedRandom<>();
        int current_mods = 0;
        List<ModInstance> matched_mods = new ArrayList<>();
        for (ModInstance modInstance : modInstances) {
            if (modInstance.fixed) continue;
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
                if (modInstance.fixed) continue;
                if (modInstance.getPool_name().equals(entryMap.getValue().getPool().getPool_name())) {
                    matching_mods++;
                }
            }

            if (matching_mods < max_mods_in_pool) {
                CraftModPool pool = entryMap.getValue().getPool();
                List<CraftedMod> candidates = pool.getListByEquipType(equipmentType, weaponType);
                if (candidates == null) candidates = Collections.emptyList();

                // === เพิ่มตรงนี้: สร้าง weighted pool ระดับ CraftedMod ===
                WeightedRandom<CraftedMod> mod_pool_for_a_material = new WeightedRandom<>();
                for (CraftedMod mod : candidates) {
                    mod_pool_for_a_material.add(mod, mod.weight);
                }
                applyWeightBoostByMaterial(mod_pool_for_a_material, equipment.getMaterialInstances());

                // placeholder ModInstance ไว้ให้ applyUniqueBoost ใช้ context (pool_name, base_material_id)
                ModInstance placeholder = new ModInstance(mod_id, material.material_id, pool.getPool_name());
                UniqueMaterialManager.applyUniqueBoost(mod_pool_for_a_material, equipment.getMaterialInstances(), placeholder);

                if (!pool.can_duplicate_mod) {
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
                            ModInstance modInstance = new ModInstance(mod_id, material.material_id, pool.getPool_name(), mod);
                            evaluateMod(equipment, horn, modInstance, entryMap.getValue(), equipment.getMaterialInstances());
                            random.add(modInstance, mod_pool_for_a_material.getWeight(mod));
                        }
                    }
                } else {
                    for (CraftedMod mod : candidates) {
                        ModInstance modInstance = new ModInstance(mod_id, material.material_id, pool.getPool_name(), mod);
                        evaluateMod(equipment, horn, modInstance, entryMap.getValue(), equipment.getMaterialInstances());
                        random.add(modInstance, mod_pool_for_a_material.getWeight(mod));
                    }
                }
            }
        }

        return random;
    }

    public static Set<ModInstance> snapshotLockedMods(List<ModInstance> modInstances) {
        Set<ModInstance> locked = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ModInstance m : modInstances) {
            if (m.locked) locked.add(m);
        }
        return locked;
    }

    public static boolean tryAffect(ModInstance modInstance) {
        if (modInstance.locked) {
            unlockMod(modInstance);
            return false;
        }
        return true;
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

    public static boolean brickCheckCatalyst(String catalyst_name, CraftedEquipment equipment) {
        CatalystFactory catalystFactory = new CatalystFactory();
        CatalystEffect effect = catalystFactory.get(catalyst_name);
        if (effect == null) return false;
        return effect.brick(equipment);
    }
}
