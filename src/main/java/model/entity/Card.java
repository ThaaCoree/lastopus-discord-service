package main.java.model.entity;

import main.java.model.modifier.BasicModifier;
import main.java.model.modifier.TransferModifier;
import main.java.model.type.CardType;
import main.java.model.type.StatType;
import main.java.model.type.StatusType;

import java.util.LinkedHashMap;
import java.util.Map;

public class Card {
    private String name;
    private Map<CardType, String> abilityName = new LinkedHashMap<>();
    private Map<CardType, String> description = new LinkedHashMap<>();
    private Map<CardType, String> statusDescription = new LinkedHashMap<>();
    private final Map<CardType, ModifierBundle> modifiers = new LinkedHashMap<>();

    public Card(String name) {
        this.name = name;
        modifiers.put(CardType.PRIMARY,new ModifierBundle());
        modifiers.put(CardType.SECONDARY,new ModifierBundle());
        abilityName.put(CardType.PRIMARY, "");
        abilityName.put(CardType.SECONDARY, "");
        description.put(CardType.PRIMARY, "");
        description.put(CardType.SECONDARY, "");
        statusDescription.put(CardType.PRIMARY, "");
        statusDescription.put(CardType.SECONDARY, "");
    }

    public Card() {
        modifiers.put(CardType.PRIMARY,new ModifierBundle());
        modifiers.put(CardType.SECONDARY,new ModifierBundle());
        abilityName.put(CardType.PRIMARY, "");
        abilityName.put(CardType.SECONDARY, "");
        description.put(CardType.PRIMARY, "");
        description.put(CardType.SECONDARY, "");
        statusDescription.put(CardType.PRIMARY, "");
        statusDescription.put(CardType.SECONDARY, "");
    }

    public void resetStatusDescription(CardType type) {
        statusDescription.put(type, "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<CardType, String> getAbilityName() {
        return abilityName;
    }

    public void setAbilityName(Map<CardType, String> abilityName) {
        this.abilityName = abilityName;
    }

    public Map<CardType, String> getDescription() {
        return description;
    }

    public void setDescription(Map<CardType, String> description) {
        this.description = description;
    }

    public Map<CardType, String> getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(Map<CardType, String> statusDescription) {
        this.statusDescription = statusDescription;
    }

    public Map<CardType, ModifierBundle> getModifiers() {
        return modifiers;
    }

    public Map<StatType, BasicModifier> getStatModifiers(CardType type) {
        return modifiers.get(type).getStatModifiers();
    }

    public Map<StatusType, BasicModifier> getStatusModifiers(CardType type) {
        return modifiers.get(type).getStatusModifiers();
    }

    public Map<Integer, TransferModifier> getTransferModifiers(CardType type) {
        return modifiers.get(type).getTransferModifiers();
    }

    public void setStatModifiers(CardType type, Map<StatType, BasicModifier> statModifiers) {
        modifiers.get(type).setStatModifiers(statModifiers);
    }

    public void setStatusModifiers(CardType type, Map<StatusType, BasicModifier> statusModifiers) {
        modifiers.get(type).setStatusModifiers(statusModifiers);
    }

    public void setTransferModifiers(CardType type, Map<Integer, TransferModifier> transferModifiers) {
        modifiers.get(type).setTransferModifiers(transferModifiers);
    }

    public void addStatusDescription(String statusDescToAdd, CardType type) {
        statusDescription.put(type, statusDescription.get(type) + statusDescToAdd);
    }
}
