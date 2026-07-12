package model.entity.items.crafted_equipments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.type.EquipmentType;
import model.type.ModifierType;
import model.type.StatType;
import model.type.StatusType;

import java.util.concurrent.ThreadLocalRandom;

public class CraftedMod {
    StatType statType;
    StatusType statusType;
    boolean negative;
    ModifierType modifierType;
    int weight;
    boolean fixed;

    CraftedMod() {

    }

    CraftedMod(StatusType type, boolean negative, ModifierType modifierType, int weight, boolean fixed) {
        statusType = type;
        this.negative = negative;
        this.modifierType = modifierType;
        this.weight = weight;
        this.fixed = fixed;
    }

    CraftedMod(StatType type, boolean negative, ModifierType modifierType, int weight, boolean fixed) {
        statType = type;
        this.negative = negative;
        this.modifierType = modifierType;
        this.weight = weight;
        this.fixed = fixed;
    }

    public CraftedMod(ModifierType modifierType, boolean negative, boolean fixed) {
        this.modifierType = modifierType;
        this.negative = negative;
        this.fixed = fixed;
    }

    public double getRandomizedModValueByTier(int tier, EquipmentType equipmentType, boolean two_handed) {
        double a = getMinModValueByTier(tier, equipmentType, two_handed);
        double b = getMaxModValueByTier(tier, equipmentType, two_handed);
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public double getMinModValueByTier(int tier, EquipmentType equipmentType, boolean two_handed) {
        double multiplier;
        if (isStatusMod()) {
            multiplier = statusType.getMin_multiplier();
        } else if (isStatMod()) {
            multiplier = statType.getMin_multiplier();
        } else {
            return 0;
        }
        return getModValueByTier(tier, equipmentType, two_handed)*multiplier;
    }

    public double getMaxModValueByTier(int tier, EquipmentType equipmentType, boolean two_handed) {
        double multiplier;
        if (isStatusMod()) {
            multiplier = statusType.getMax_multiplier();
        } else if (isStatMod()) {
            multiplier = statType.getMax_multiplier();
        } else {
            return 0;
        }
        return getModValueByTier(tier, equipmentType, two_handed)*multiplier;
    }

    public double getModValueByTier(int tier, EquipmentType equipmentType, boolean two_handed) {
        double base_value;
        double growth;
        double exponent;
        double equipment_multiplier = 1;
        if (equipmentType == EquipmentType.HELMET || equipmentType == EquipmentType.BOOTS || equipmentType == EquipmentType.GLOVES) {
            equipment_multiplier = 0.6;
        } else if (equipmentType == EquipmentType.ARMOR) {
            equipment_multiplier = 0.8;
        } else if (equipmentType == EquipmentType.WEAPON) {
            if (two_handed) {
                equipment_multiplier = 1;
            } else {
                equipment_multiplier = 0.75;
            }
        } else if (equipmentType == EquipmentType.ACCESSORY) {
            equipment_multiplier = 0.5;
        }

        if (isStatusMod()) {
            base_value = statusType.getBase_value();
            growth = statusType.getGrowth();
            exponent = statusType.getExponent();
        } else if (isStatMod()) {
            base_value = statType.getBase_value();
            growth = statType.getGrowth();
            exponent = statType.getExponent();
        } else {
            return 0;
        }
        double to_return = base_value + growth * Math.pow(tier, exponent);
        if (negative) {
            to_return *= -1;
        }
        return to_return * equipment_multiplier;
    }

    @JsonIgnore
    public String getAffectingModString() {
        if (isStatMod()) {
            return statType.writeAsString();
        } else {
            return statusType.writeAsString();
        }
    }

    @JsonIgnore
    public StatType getAffectingModStat() {
        return statType;
    }

    @JsonIgnore
    public StatusType getAffectingModStatus() {
        return statusType;
    }

    @JsonIgnore
    public boolean isStatusMod() {
        return statusType != null;
    }

    @JsonIgnore
    public boolean isStatMod() {
        return statType != null;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public ModifierType getModifierType() {
        return modifierType;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public StatType getStatType() {
        return statType;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setStatType(StatType statType) {
        this.statType = statType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public void setModifierType(ModifierType modifierType) {
        this.modifierType = modifierType;
    }
}
