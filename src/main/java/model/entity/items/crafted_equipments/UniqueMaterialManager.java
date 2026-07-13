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
            applyNitron(materialInstance, modInstance);
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
            if (materialInstance.material.getName().equals("Claw of Light Dragon")) return;
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
            applyWingOfLightDragon(materialInstance, modInstance);
        }
    }

    public static void applyWingOfLightDragon(MaterialInstance materialInstance, ModInstance modInstance) {
        if (!materialInstance.getMaterial().getName().equals("Wing of Light Dragon")) return;
        modInstance.multiplyByTag(StatTag.DEFENSE, 1);
    }

    public static void applyNitron(MaterialInstance materialInstance, ModInstance modInstance) {
        if (!materialInstance.getMaterial().getName().equals("Nitron")) return;
        if (materialInstance.getMaterial_role() != MaterialRole.BASE) return;
        double random = ThreadLocalRandom.current().nextDouble(1.5, 2.5);

        modInstance.multiplyFinal_value(random);
        modInstance.multiplyFinal_value(ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
    }

    public void setModInstance(ModInstance modInstance) {
        this.modInstance = modInstance;
    }
}

