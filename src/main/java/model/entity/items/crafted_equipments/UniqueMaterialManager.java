package model.entity.items.crafted_equipments;

import model.type.MaterialRole;
import model.type.StatTag;
import util.WeightedRandom;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UniqueMaterialManager {
    ModInstance modInstance;

    public UniqueMaterialManager() {
    }

    public static void applyUniqueBaseMultitude(List<MaterialInstance> materialInstances, ModInstance modInstance) {
        for (MaterialInstance materialInstance : materialInstances) {
            if (modInstance == null) continue;
            if (materialInstance.getMaterial_role() != MaterialRole.BASE) continue;
            modInstance.addBoostedMaterial(materialInstance.material_id);
            applyNitron(materialInstance, modInstance, materialInstances);
        }
    }

    public static void applyUniqueBoost(WeightedRandom<CraftedMod> weightedRandom, List<MaterialInstance> materialInstances, ModInstance modInstance) {
        for (MaterialInstance materialInstance : materialInstances) {
            if (modInstance == null) continue;
            if (!modInstance.boost_material_ids.contains(materialInstance.material_id)) continue;
            if (materialInstance.material_role == MaterialRole.BASE) continue;
            modInstance.addBoostedMaterial(materialInstance.material_id);
            applyClawOfLightDragon(weightedRandom, materialInstance, materialInstances);
        }
    }

    public static void applyClawOfLightDragon(WeightedRandom<CraftedMod> weightedRandom, MaterialInstance materialInstance, List<MaterialInstance> materialInstances) {
        //have to rework a bit
            if (isBoostMaterial(materialInstance, "Claw of Light Dragon")) return;
            Crafter.applyWeightBoostByTag(weightedRandom, StatTag.STRIKE, 5);
            Crafter.applyWeightBoostByTag(weightedRandom, StatTag.DEFENSE, 5);
            if (materialInstances.contains(materialInstance) && materialInstances.indexOf(materialInstance) %2 == 1) {
                //this point to rework
                Crafter.applyWeightBoostByTag(weightedRandom, StatTag.PHYSICAL, -1);
            } else {
                //and this point
                Crafter.applyWeightBoostByTag(weightedRandom, StatTag.ACCURATE, -1);
            }
    }

    public static void applyUniqueBoostMultitude(List<MaterialInstance> materialInstances, ModInstance modInstance) {
        for (MaterialInstance materialInstance : materialInstances) {
            if (modInstance == null) continue;
            if (!modInstance.boost_material_ids.contains(materialInstance.material_id)) continue;
            modInstance.addBoostedMaterial(materialInstance.material_id);
            CraftingMaterial material = materialInstance.material;

        }
    }

    public static void applyNitron(MaterialInstance materialInstance, ModInstance modInstance,
                                   List<MaterialInstance> materialInstances) {
        if (!isBaseMaterial(materialInstance, "Nitron")) return;

        int nitrons = 1;
        MaterialInstance last = null;
        for (MaterialInstance instance : materialInstances) {
            if (isBaseMaterial(instance, "Nitron")) {
                nitrons++;
                last = instance;
            }
        }

        modInstance.multiplyFinal_value(ThreadLocalRandom.current().nextDouble(1.5, 2.5));

        if (materialInstance != last) return;

        WeightedRandom<Boolean> negative = new WeightedRandom<>();
        negative.add(true, 2);
        negative.add(false, nitrons);
        if (negative.roll()) {
            modInstance.negativeFinal_value();
        }
    }

    private static boolean isBaseMaterial(MaterialInstance mi, String name) {
        return mi.getMaterial().getName().equalsIgnoreCase(name)
                && mi.getMaterial_role() == MaterialRole.BASE;
    }

    private static boolean isBoostMaterial(MaterialInstance mi, String name) {
        return mi.getMaterial().getName().equalsIgnoreCase(name)
                && mi.getMaterial_role() == MaterialRole.BOOST;
    }

    public void setModInstance(ModInstance modInstance) {
        this.modInstance = modInstance;
    }
}

