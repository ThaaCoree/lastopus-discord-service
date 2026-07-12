package model.entity.items.crafted_equipments;

import model.type.MaterialRole;
import model.type.StatTag;
import util.WeightedRandom;

import java.util.List;

public class UniqueMaterialManager {
    WeightedRandom<CraftedMod> weightedRandom;
    List<CraftedEquipment.MaterialInstance> materialInstances;
    CraftedEquipment.ModInstance modInstance;

    public UniqueMaterialManager(WeightedRandom<CraftedMod> weightedRandom, List<CraftedEquipment.MaterialInstance> materialInstances) {
        this.weightedRandom = weightedRandom;
        this.materialInstances = materialInstances;
    }

    public void applyUniqueBoost() {
        for (CraftedEquipment.MaterialInstance materialInstance : materialInstances) {
            if (modInstance == null) continue;
            if (modInstance.boost_material_ids.contains( materialInstance.material_id)) continue;
            modInstance.addBoostedMaterial(materialInstance.material_id);
            if (materialInstance.material_role == MaterialRole.BASE) continue;
            CraftingMaterial material = materialInstance.material;
            applyClawOfLightDragon(materialInstance);
        }
    }

    public void applyClawOfLightDragon(CraftedEquipment.MaterialInstance materialInstance) {
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

    public void applyUniqueMultitude() {
        for (CraftedEquipment.MaterialInstance materialInstance : materialInstances) {
            if (modInstance == null) continue;
            if (modInstance.boost_material_ids.contains( materialInstance.material_id)) continue;
            modInstance.addBoostedMaterial(materialInstance.material_id);
            CraftingMaterial material = materialInstance.material;
            applyWingOfLightDragon(material);
        }
    }

    public void applyWingOfLightDragon(CraftingMaterial material) {
        if (!material.getName().equals("Wing of Light Dragon")) return;
        modInstance.multiplyByTag(StatTag.DEFENSE, 1);
    }

    public void setModInstance(CraftedEquipment.ModInstance modInstance) {
        this.modInstance = modInstance;
    }
}

