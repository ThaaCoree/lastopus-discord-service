package main.java.model.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import main.java.model.modifier.BasicModifier;
import main.java.model.modifier.TransferModifier;
import main.java.model.type.*;

import java.util.Map;

public class Conditions {
    private String name;
    private String description;
    private String statusDescription;
    private final ModifierBundle modifiers = new ModifierBundle();
    private ConditionType conditionType;
    private ConditionTierType conditionTierType;

    public Conditions(String name) {
        this.name = name;
        description = "";
        statusDescription = "";
        conditionType = ConditionType.NEUTRAL;
        conditionTierType = ConditionTierType.BASIC;
    }

    public Conditions() {
        name = "NONAME";
        description = "";
        statusDescription = "";
        conditionType = ConditionType.NEUTRAL;
        conditionTierType = ConditionTierType.BASIC;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public ConditionTierType getConditionTierType() {
        return conditionTierType;
    }

    public void setConditionTierType(ConditionTierType conditionTierType) {
        this.conditionTierType = conditionTierType;
    }

    @JsonIgnore
    public BasicModifier getStatModifiers(StatType type) {
        return modifiers.getStatModifierSafe(type);
    }

    @JsonIgnore
    public BasicModifier getStatusModifiers(StatusType type) {
        return modifiers.getStatusModifierSafe(type);
    }

    @JsonIgnore
    public Map<Integer, TransferModifier> getTransferModifiers() {
        return modifiers.getTransferModifiers();
    }

    public void setStatModifiers(Map<StatType, BasicModifier> statModifiers) {
        modifiers.setStatModifiers(statModifiers);
    }

    public void setStatusModifiers(Map<StatusType, BasicModifier> statusModifiers) {
        modifiers.setStatusModifiers(statusModifiers);
    }

    public void setTransferModifiers(Map<Integer, TransferModifier> transferModifiers) {
        modifiers.setTransferModifiers(transferModifiers);
    }

    public void addStatusDescription(String statusDescToAdd) {
        statusDescription = statusDescription + statusDescToAdd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModifierBundle getModifiers() {
        return modifiers;
    }
}
