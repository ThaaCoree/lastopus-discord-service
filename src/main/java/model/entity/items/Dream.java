package main.java.model.entity.items;

import main.java.model.entity.ModifierBundle;
import main.java.model.entity.skills.SkillInstance;
import main.java.model.modifier.BasicModifier;
import main.java.model.modifier.TransferModifier;
import main.java.model.type.*;

import java.util.HashMap;
import java.util.Map;

public class Dream extends Item {
    private NodeType nodeType;
    private final ModifierBundle modifiers = new ModifierBundle();
    private final Map<String, SkillInstance> skills = new HashMap<>();

    public Dream(String name) {
        this.setName(name);
        setItemType(ItemType.NONE);
        setDescription("");
        setStatusDescription("");
        setLore("");
        setPrice("");
        nodeType = NodeType.SMALL;
    }

    public Dream() {
        setName("");
        setItemType(ItemType.NONE);
        setDescription("");
        setStatusDescription("");
        setLore("");
        setPrice("");
        nodeType = NodeType.SMALL;
    }

    public Map<StatType, BasicModifier> getStatModifiers() {
        return modifiers.getStatModifiers();
    }

    public Map<StatusType, BasicModifier> getStatusModifiers() {
        return modifiers.getStatusModifiers();
    }

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

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Map<String, SkillInstance> getSkills() {
        return skills;
    }

    public ModifierBundle getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "nodeType=" + nodeType +
                ", modifiers=" + modifiers.getStatModifiers() +
                ", " + modifiers.getStatusModifiers() +
                ", " + modifiers.getTransferModifiers() +
                "}";
    }
}
