package model.entity;

import model.modifier.BasicModifier;
import model.modifier.SkillModifier;
import model.modifier.TransferModifier;
import model.type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassiveNode {
    private int id; // ex: "001"
    private String name;
    private String description;
    private String statusDescription;
    private String lore;
    private List<Integer> connectedNodes;
    private double x;
    private double y;
    private ModifierBundle modifiers = new ModifierBundle();
    private Map<EquipmentType, Double> equipmentSlotMult;
    private NodeType nodeType;
    private int statusPoints;
    private boolean isDream;

    public PassiveNode() {
        this.id = 0;
        name = null;
        description = "";
        statusDescription = "";
        lore = "";
        connectedNodes = new ArrayList<>();
        x = 0;
        y = 0;
        equipmentSlotMult = new HashMap<>();
        nodeType = NodeType.SMALL;
    }

    public boolean isDream() {
        return isDream;
    }

    public void setDream(boolean dream) {
        isDream = dream;
    }

    public void setModifiers(ModifierBundle modifiers) {
        this.modifiers = modifiers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void addStatusDescription(String statusDescToAdd) {
        statusDescription = statusDescription + statusDescToAdd;
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

    public Map<SkillType, SkillModifier> getSkillModifiers() {
        return modifiers.getSkillModifiers();
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

    public void setSkillModifiers(Map<SkillType, SkillModifier> skillModifiers) {
        modifiers.setSkillModifiers(skillModifiers);
    }

    public List<Integer> getConnectedNodes() {
        return connectedNodes;
    }

    public void setConnectedNodes(List<Integer> connectedNodes) {
        this.connectedNodes = connectedNodes;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void addConnectedNodes(int num) {
        connectedNodes.add(num);
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Map<EquipmentType, Double> getEquipmentSlotMult() {
        return equipmentSlotMult;
    }

    public void setEquipmentSlotMult(Map<EquipmentType, Double> equipmentSlotMult) {
        this.equipmentSlotMult = equipmentSlotMult;
    }

    public ModifierBundle getModifiers() {
        return modifiers;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public int getStatusPoints() {
        return statusPoints;
    }

    public void setStatusPoints(int statusPoints) {
        this.statusPoints = statusPoints;
    }
}

