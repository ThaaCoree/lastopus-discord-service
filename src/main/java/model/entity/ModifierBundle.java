package model.entity;

import model.modifier.BasicModifier;
import model.modifier.SkillModifier;
import model.modifier.TransferModifier;
import model.type.SkillType;
import model.type.StatType;
import model.type.StatusType;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModifierBundle {
    private Map<StatType, BasicModifier> statModifiers = new LinkedHashMap<>();
    private Map<StatusType, BasicModifier> statusModifiers = new LinkedHashMap<>();
    private Map<Integer, TransferModifier> transferModifiers = new LinkedHashMap<>();
    private Map<SkillType, SkillModifier> skillModifiers = new LinkedHashMap<>();

    public ModifierBundle () {
    }

    public void setAll(ModifierBundle other) {
        this.statModifiers.clear();
        this.statModifiers.putAll(other.statModifiers);

        this.statusModifiers.clear();
        this.statusModifiers.putAll(other.statusModifiers);

        this.transferModifiers.clear();
        this.transferModifiers.putAll(other.transferModifiers);

        this.skillModifiers.clear();
        this.skillModifiers.putAll(other.skillModifiers);
    }

    public ModifierBundle deepcopy() {
        ModifierBundle copy = new ModifierBundle();

        // statModifiers
        for (var e : this.statModifiers.entrySet()) {
            copy.statModifiers.put(e.getKey(), e.getValue().copy());
        }

        // statusModifiers
        for (var e : this.statusModifiers.entrySet()) {
            copy.statusModifiers.put(e.getKey(), e.getValue().copy());
        }

        // transferModifiers
        for (var e : this.transferModifiers.entrySet()) {
            copy.transferModifiers.put(e.getKey(), e.getValue().copy());
        }

        // skillModifiers
        for (var e : this.skillModifiers.entrySet()) {
            copy.skillModifiers.put(e.getKey(), e.getValue().copy());
        }

        return copy;
    }

    public void multiplyAllModifiers (double multiplier) {
        multiplyAllStatModifiers(multiplier);
        multiplyAllStatusModifiers(multiplier);
        multiplyAllStatusTransferModifiers(multiplier);
        multiplyAllStatTransferModifiers(multiplier);
    }

    public void multiplyAllStatusTransferModifiers(double multiplier) {
        for (TransferModifier modifier : transferModifiers.values()) {
            if (modifier == null) continue;
            if (modifier.getSourceStatus() == null) continue;
            modifier.setTransferRatio(modifier.getTransferRatio() * multiplier);
        }
    }

    public void multiplyAllStatTransferModifiers(double multiplier) {
        for (TransferModifier modifier : transferModifiers.values()) {
            if (modifier == null) continue;
            if (modifier.getSourceStat() == null) continue;
            modifier.setTransferRatio(modifier.getTransferRatio() * multiplier);
        }
    }

    public void multiplyAllStatusModifiers(double multiplier) {
        for (BasicModifier modifier : statusModifiers.values()) {
            if (modifier == null) continue;
            modifier.setFlat(modifier.getFlat() * multiplier);
            modifier.setGlobalMult(modifier.getGlobalMult() * multiplier);
            modifier.setPassiveMult(modifier.getPassiveMult() * multiplier);
            modifier.setEquipmentMult(modifier.getEquipmentMult() * multiplier);
        }
    }

    public void multiplyAllStatModifiers(double multiplier) {
        for (BasicModifier modifier : statModifiers.values()) {
            if (modifier == null) continue;
            modifier.setFlat(modifier.getFlat() * multiplier);
            modifier.setGlobalMult(modifier.getGlobalMult() * multiplier);
            modifier.setPassiveMult(modifier.getPassiveMult() * multiplier);
            modifier.setEquipmentMult(modifier.getEquipmentMult() * multiplier);
        }
    }

    public Map<StatType, BasicModifier> getStatModifiers() {
        return statModifiers;
    }

    public BasicModifier getStatModifierSafe(StatType type) {
        return statModifiers.computeIfAbsent(type, k -> new BasicModifier());
    }

    public Map<StatusType, BasicModifier> getStatusModifiers() {
        return statusModifiers;
    }

    public BasicModifier getStatusModifierSafe(StatusType type) {
        return statusModifiers.computeIfAbsent(type, k -> new BasicModifier());
    }

    public Map<Integer, TransferModifier> getTransferModifiers() {
        return transferModifiers;
    }

    public Map<SkillType, SkillModifier> getSkillModifiers() {
        return skillModifiers;
    }

    public SkillModifier getSkillModifierSafe(SkillType type) {
        return skillModifiers.computeIfAbsent(type, k -> new SkillModifier());
    }

    public void setSkillModifiers(Map<SkillType, SkillModifier> skillModifiers) {
        this.skillModifiers = skillModifiers;
    }

    public void setStatModifiers(Map<StatType, BasicModifier> statModifiers) {
        this.statModifiers = statModifiers;
    }

    public void setStatusModifiers(Map<StatusType, BasicModifier> statusModifiers) {
        this.statusModifiers = statusModifiers;
    }

    public void setTransferModifiers(Map<Integer, TransferModifier> transferModifiers) {
        this.transferModifiers = transferModifiers;
    }

    public void addTransferModifier( TransferModifier tm) {
        for (int i = 1; i <= 100; i++) {
            if (!this.transferModifiers.containsKey(i)) {
                this.transferModifiers.put(i, tm);
                break;
            }
        }
    }
}
