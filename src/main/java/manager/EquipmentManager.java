package manager;

import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.units.Unit;
import model.type.EquipmentType;
import model.type.WeaponType;

import java.util.ArrayList;
import java.util.List;

public class EquipmentManager {
    private final Unit unit;

    public EquipmentManager(Unit unit) {
        this.unit = unit;
        initializeDefaultSlots();
    }

    private void initializeDefaultSlots() {
        addEquipmentToSlot(1,EquipmentType.HELMET,null);
        addEquipmentToSlot(2,EquipmentType.ARMOR,null);
        addEquipmentToSlot(3,EquipmentType.BOOTS,null);
        addEquipmentToSlot(4,EquipmentType.GLOVES,null);
        addEquipmentToSlot(5,EquipmentType.ACCESSORY,null);
        addEquipmentToSlot(6,EquipmentType.ACCESSORY,null);
        addEquipmentToSlot(7,EquipmentType.WEAPON,null);
        addEquipmentToSlot(8,EquipmentType.WEAPON,null);
        addEquipmentToSlot(9,EquipmentType.BACKPACK,null);
    }

    private void addEquipmentToSlot(int slot, EquipmentType type, Equipment equipment) {
        // ค้นหาว่า slot นั้นมี Map ของ EquipmentType หรือยัง
        unit.getEquipmentSlots().putIfAbsent(slot, new EquipmentSlot(type));
        // เพิ่ม Equipment ตามประเภทที่เลือก
        unit.getEquipmentSlots().get(slot).setEquipment(equipment);
    }

    public void addCustomSlot(EquipmentType equipmentType) {
        // หาตำแหน่งว่างใน slots
        int newSlot = findAvailableSlot();

        // ถ้าเจอ slot ว่าง
        if (newSlot != -1) {
            unit.getEquipmentSlots().put(newSlot, new EquipmentSlot(equipmentType));
            System.out.println("Added custom slot: " + newSlot + " with type " + equipmentType);
        } else {
            System.out.println("No available slot for " + equipmentType);
        }
    }

    private int findAvailableSlot() {
        int slot = 1;
        while (unit.getEquipmentSlots().containsKey(slot)) {
            slot++;  // คำนวณหาตำแหน่งว่าง
        }
        return slot <= 15 ? slot : -1;  // ถ้ามี slot มากกว่า 15 ก็จะ return -1 เพื่อบอกว่าไม่มีที่ว่าง
    }

    public void removeCustomSlot(int slot) {
        unit.getEquipmentSlots().remove(slot);
    }

    public void equip(int slot, Equipment equipment) {
        EquipmentSlot currentSlot = unit.getEquipmentSlots().get(slot);
        if (currentSlot == null) {
            System.out.println("Invalid slot number");
            return;
        }
        EquipmentType slotType = currentSlot.getEquipmentType();
        if (slotType == null) {
            System.out.println("Slot type is not defined");
            return;
        }
        if (equipment.getEquipmentType() == slotType) {
            currentSlot.setEquipment(equipment);
            unit.getInventoryManager().removeItem(equipment.getName());
            System.out.println("Equipped " + equipment.getName() + " to slot " + slot);
        } else {
            System.out.println("Equipment type does not match the slot type.");
        }
        handleMixTwoHanded(unit);
        unit.calculateEverything();
    }

    public void equip(Equipment equipment, Integer sub_slot) {
        int slot = 0;
        if (sub_slot != null) {
            sub_slot -= 1;
        }
        switch (equipment.getEquipmentType()) {
            case HELMET -> slot = 1;
            case ARMOR -> slot = 2;
            case BOOTS -> slot = 3;
            case GLOVES -> slot = 4;
            case ACCESSORY -> slot = 5+sub_slot;
            case WEAPON -> slot = 7+sub_slot;
            case BACKPACK -> slot = 9;
        }
        unequip(slot);
        equip(slot, equipment);
        unit.calculateEverything();
    }

    public void unequip(int slot) {
        EquipmentSlot currentSlot = unit.getEquipmentSlots().get(slot);
        if (currentSlot == null) {
            System.out.println("Invalid slot number");
            return;
        }

        Equipment removed = currentSlot.getEquipment();
        if (removed != null) {
            currentSlot.setEquipment(null);
            unit.getInventoryManager().addItem(removed);
            System.out.println("Unequipped " + removed.getName() + " from slot " + slot);
        } else {
            System.out.println("No equipment to unequip from slot " + slot);
        }
        handleMixTwoHanded(unit);
        unit.calculateEverything();
    }

    public Equipment getEquippedItem(int slot) {
        EquipmentSlot equipmentSlot = unit.getEquipmentSlots().get(slot);
        if (equipmentSlot == null) {
            System.out.println("Invalid slot number: " + slot);
            return null;
        }
        handleMixTwoHanded(unit);
        return equipmentSlot.getEquipment();
    }

    public void handleMixTwoHanded(Unit unit) {
        Equipment equipment1 = unit.getEquipmentSlots().get(7).getEquipment();
        Equipment equipment2 = unit.getEquipmentSlots().get(8).getEquipment();
        if (equipment1 == null || equipment2 == null) {
            unit.setMixTwoHanded(false);
            return;
        }
        List<WeaponType> weaponType = new ArrayList<>();
        weaponType.add(equipment1.getWeaponType());
        weaponType.add(equipment2.getWeaponType());

        for (WeaponType type : weaponType) {
            if (type == WeaponType.SHIELD ||
                type == WeaponType.CHAIN) {
                unit.setMixTwoHanded(false);
                return;
            }
        }
        unit.setMixTwoHanded(true);
    }
}
