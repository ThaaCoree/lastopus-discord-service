package model.entity.items.crafted_equipments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.type.*;

import java.util.ArrayList;
import java.util.List;

public class CraftModPool {
    String pool_name = "";
    List<CraftedMod> weaponMods = new ArrayList<>();
    List<CraftedMod> armorMods = new ArrayList<>();
    List<CraftedMod> accessoryMods = new ArrayList<>();
    boolean can_duplicate_mod = false;
    String description = "";
    String fixed_mods_description = "";

    public CraftModPool(String pool_name) {
        this.pool_name = pool_name;
    }

    public CraftModPool() {

    }

    public void addWeaponMod(StatusType type, int weight, boolean is_negative, ModifierType modifierType, boolean fixed_mod) {
        weaponMods.add(new CraftedMod(type, is_negative, modifierType, weight, fixed_mod));
    }

    public void addWeaponMod(StatType type, int weight, boolean is_negative, ModifierType modifierType, boolean fixed_mod) {
        weaponMods.add(new CraftedMod(type, is_negative, modifierType, weight, fixed_mod));
    }

    public void addArmorMod(StatusType type, int weight, boolean is_negative, ModifierType modifierType, boolean fixed_mod) {
        armorMods.add(new CraftedMod(type, is_negative, modifierType, weight, fixed_mod));
    }

    public void addArmorMod(StatType type, int weight, boolean is_negative, ModifierType modifierType, boolean fixed_mod) {
        armorMods.add(new CraftedMod(type, is_negative, modifierType, weight, fixed_mod));
    }

    public void addAccessoryMod(StatusType type, int weight, boolean is_negative, ModifierType modifierType, boolean fixed_mod) {
        accessoryMods.add(new CraftedMod(type, is_negative, modifierType, weight, fixed_mod));
    }

    public void addAccessoryMod(StatType type, int weight, boolean is_negative, ModifierType modifierType, boolean fixed_mod) {
        accessoryMods.add(new CraftedMod(type, is_negative, modifierType, weight, fixed_mod));
    }

    @JsonIgnore
    public List<CraftedMod> getListByEquipType(EquipmentType equipmentType, WeaponType weaponType) {
        if (equipmentType == EquipmentType.WEAPON && weaponType != WeaponType.SHIELD && weaponType != WeaponType.CHAIN) {
            return weaponMods;
        } else if (equipmentType == EquipmentType.ACCESSORY) {
            return accessoryMods;
        } else if (equipmentType == EquipmentType.HELMET || equipmentType == EquipmentType.ARMOR ||
                equipmentType == EquipmentType.BOOTS || equipmentType == EquipmentType.GLOVES ||
                weaponType == WeaponType.SHIELD || weaponType == WeaponType.CHAIN) {
            return armorMods;
        }

        else return null;
    }

    public void clearWeaponMod() {
        weaponMods.clear();
    }

    public void clearArmorMod() {
        armorMods.clear();
    }

    public void clearAccessoryMod() {
        accessoryMods.clear();
    }

    public String getPool_name() {
        return pool_name;
    }

    public void setPool_name(String pool_name) {
        this.pool_name = pool_name;
    }

    public String getFixed_mods_description() {
        return fixed_mods_description;
    }

    public void setFixed_mods_description(String fixed_mods_description) {
        this.fixed_mods_description = fixed_mods_description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCan_duplicate_mod() {
        return can_duplicate_mod;
    }

    public void setCan_duplicate_mod(boolean can_duplicate_mod) {
        this.can_duplicate_mod = can_duplicate_mod;
    }

    public List<CraftedMod> getAccessoryMods() {
        return accessoryMods;
    }

    public List<CraftedMod> getArmorMods() {
        return armorMods;
    }

    public List<CraftedMod> getWeaponMods() {
        return weaponMods;
    }

    public void setAccessoryMods(List<CraftedMod> accessoryMods) {
        this.accessoryMods = accessoryMods;
    }

    public void setArmorMods(List<CraftedMod> armorMods) {
        this.armorMods = armorMods;
    }

    public void setWeaponMods(List<CraftedMod> weaponMods) {
        this.weaponMods = weaponMods;
    }
}
