package model.entity.items.crafted_equipments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.type.EquipmentType;
import model.type.StatTag;
import model.type.WeaponType;
import util.WeightedRandom;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    public ModInstance() {

    }
    public ModInstance(int mod_id, int base_material_id, String pool_name, CraftedMod mod) {
        this.mod_id = mod_id;
        this.base_material_id = base_material_id;
        this.pool_name = pool_name;
        this.mod = mod;
    }

    public ModInstance(int mod_id, int base_material_id, String pool_name) {
        this.mod_id = mod_id;
        this.base_material_id = base_material_id;
        this.pool_name = pool_name;
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

    @JsonIgnore
    public String getMinRoll(EquipmentType equipmentType, WeaponType weaponType) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(mod.getMinModValueByTier(tier, equipmentType, weaponType.twoHanded()));
    }

    @JsonIgnore
    public String getMaxRoll(EquipmentType equipmentType, WeaponType weaponType) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(mod.getMaxModValueByTier(tier, equipmentType, weaponType.twoHanded()));
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

    public void multiplyFinal_value(double multiply) {
        final_value *= multiply;
    }

    public CraftedMod getMod() {
        return mod;
    }

    public void setMod(CraftedMod mod) {
        this.mod = mod;
    }
}