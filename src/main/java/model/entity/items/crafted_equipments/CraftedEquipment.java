package model.entity.items.crafted_equipments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.entity.items.Equipment;
import model.type.EquipmentType;
import model.type.MaterialRole;
import model.type.StatTag;
import util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;

public class CraftedEquipment extends Equipment {
    String id;
    int mod_id = 0;
    private int next_material_id = 0;
    private int next_catalyst_id = 0;
    List<ModInstance> modInstances = new ArrayList<>();
    List<MaterialInstance> materialInstances = new ArrayList<>();
    List<CatalystInstance> catalystInstances = new ArrayList<>();

    public CraftedEquipment(String name) {
        super(name);
    }

    public void addMaterial(CraftingMaterial material, MaterialRole role) {
        MaterialInstance instance = new MaterialInstance (next_material_id++, material, role);
        materialInstances.add(instance);
    }

    public void removeMaterial(MaterialInstance materialInstance) {
        materialInstances.remove(materialInstance);
    }

    public void addCatalyst(String catalyst_name) {
        CatalystInstance instance = new CatalystInstance (next_catalyst_id++, catalyst_name);
        catalystInstances.add(instance);
    }

    public void resetMaterial() {
        materialInstances.clear();
    }

    public List<MaterialInstance> getMaterialInstances() {
        return materialInstances;
    }

    public void setMaterialInstances(List<MaterialInstance> materialInstances) {
        this.materialInstances = materialInstances;
    }

    public List<CatalystInstance> getCatalystInstances() {
        return catalystInstances;
    }

    public void setCatalystInstances(List<CatalystInstance> catalystInstances) {
        this.catalystInstances = catalystInstances;
    }

    public List<ModInstance> getModInstances() {
        return modInstances;
    }

    @JsonIgnore
    public int getNext_catalyst_idAndIncrement() {
        return next_catalyst_id++;
    }

    public class MaterialInstance {
        int material_id;
        CraftingMaterial material;
        MaterialRole material_role;

        public MaterialInstance(int material_index, CraftingMaterial material, MaterialRole material_role) {
            this.material_id = material_index;
            this.material = material;
            this.material_role = material_role;
        }

        public int getMaterial_id() {
            return material_id;
        }

        public void setMaterial_id(int material_id) {
            this.material_id = material_id;
        }

        public CraftingMaterial getMaterial() {
            return material;
        }

        public void setMaterial(CraftingMaterial material) {
            this.material = material;
        }

        public MaterialRole getMaterial_role() {
            return material_role;
        }

        public void setMaterial_role(MaterialRole material_role) {
            this.material_role = material_role;
        }
    }

    public class CatalystInstance {
        int catalyst_id;
        String catalyst_name;

        public CatalystInstance(int catalyst_index, String catalyst_name) {
            this.catalyst_id = catalyst_index;
            this.catalyst_name = catalyst_name;
        }
    }

    public class ModInstance {
        int mod_id;
        int base_material_id;
        String pool_name;
        List<Integer> boost_material_ids = new ArrayList<>();
        List<Integer> catalyst_ids = new ArrayList<>();
        int tier;
        double base_rolled;
        double final_value;
        CraftedMod mod;

        public ModInstance(int mod_id, int base_material_index, String pool_name, CraftedMod mod) {
            this.mod_id = mod_id;
            this.base_material_id = base_material_index;
            this.pool_name = pool_name;
            this.mod = mod;
        }

        public void randomizeTierAndBaseValue(int min_tier, int max_tier, EquipmentType equipmentType, boolean two_handed) {
            randomTier(min_tier, max_tier);
            rerollBase(equipmentType, two_handed);
            resetFinalValue();
        }

        public void randomTier(int min_tier, int max_tier) {
            WeightedRandom<Integer> weightedRandom = new WeightedRandom<>();
            for (int i = max_tier; i >= min_tier; i--) {
                weightedRandom.add(i, (max_tier - i + 1) * 20);
            }
            tier = weightedRandom.roll();
        }

        public void resetFinalValue() {
            final_value = base_rolled;
        }

        public void rerollBase(EquipmentType equipmentType, boolean two_handed) {
            base_rolled = mod.getRandomizedModValueByTier(tier, equipmentType, two_handed);
            final_value = base_rolled;
        }

        public void addBoostedMaterial(int material_index) {
            boost_material_ids.add(material_index);
        }

        public void addCatalyst(int catalyst_index) {
            catalyst_ids.add(catalyst_index);
        }

        public void multiplyByTag(StatTag statTag, int multiplier) {
            if (mod.isStatusMod() && mod.statusType.hasTag(statTag)) {
                final_value *= multiplier+1;
            }
            if (mod.isStatMod() && mod.statType.hasTag(statTag)) {
                final_value *= multiplier+1;
            }
        }

        public int getMod_id() {
            return mod_id;
        }

        public int getBase_material_id() {
            return base_material_id;
        }

        public String getPool_name() {
            return pool_name;
        }

        public List<Integer> getBoost_material_ids() {
            return boost_material_ids;
        }

        public List<Integer> getCatalyst_ids() {
            return catalyst_ids;
        }

        public int getTier() {
            return tier;
        }

        public double getBase_rolled() {
            return base_rolled;
        }

        public double getFinal_value() {
            return final_value;
        }

        public CraftedMod getMod() {
            return mod;
        }
    }
}
