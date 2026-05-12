package model.entity.items;

import com.fasterxml.jackson.annotation.JsonTypeName;
import model.entity.ModifierBundle;
import model.entity.skills.SkillInstance;
import model.modifier.BasicModifier;
import model.modifier.TransferModifier;
import model.type.*;

import java.util.HashMap;
import java.util.Map;

    public class Equipment extends Item {
        private EquipmentType equipmentType;
        private WeaponType weaponType;
        private ModifierBundle modifiers = new ModifierBundle();
        private Map<String, SkillInstance> skills = new HashMap<>();
        private int backpackSlot = 0;

        public Equipment(String name) {
            this.setName(name);
            setItemType(ItemType.NONE);
            setDescription("");
            setStatusDescription("");
            setLore("");
            setPrice("");
            equipmentType = EquipmentType.WEAPON;
            weaponType = WeaponType.NOT_A_WEAPON;
            backpackSlot = 0;
        }

        public Equipment() {
            setName("");
            setItemType(ItemType.NONE);
            setDescription("");
            setStatusDescription("");
            setLore("");
            setPrice("");
            equipmentType = EquipmentType.WEAPON;
            weaponType = WeaponType.NOT_A_WEAPON;
            backpackSlot = 0;
        }

        public EquipmentType getEquipmentType() {
            return equipmentType;
        }

        public WeaponType getWeaponType() {
            return weaponType;
        }

        public void setEquipmentType(EquipmentType equipmentType) {
            this.equipmentType = equipmentType;
        }

        public void setWeaponType(WeaponType weaponType) {
            this.weaponType = weaponType;
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

        public Map<String, SkillInstance> getSkills() {
            return skills;
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

        public int getBackpackSlot() {
            return backpackSlot;
        }

        public void setBackpackSlot(int backpackSlot) {
            this.backpackSlot = backpackSlot;
        }

        public ModifierBundle getModifiers() {
            return modifiers;
        }

        @Override
        public String toString() {
            return "Equipment{" +
                    "equipmentType=" + equipmentType +
                    ", weaponType=" + weaponType +
                    ", modifiers=" + modifiers.getStatModifiers() +
                    ", " + modifiers.getStatusModifiers() +
                    ", " + modifiers.getTransferModifiers() +
                    "}";
        }
    }
