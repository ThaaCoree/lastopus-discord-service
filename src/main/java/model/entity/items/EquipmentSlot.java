package model.entity.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.type.EquipmentType;

public class EquipmentSlot {
    private EquipmentType equipmentType;
    private Equipment equipment;

    public EquipmentSlot(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
        this.equipment = null;  // เริ่มต้นไม่มีอุปกรณ์
    }

    public EquipmentSlot() {

    }

    public EquipmentSlot copy() {
        EquipmentSlot copy = new EquipmentSlot();
        copy.equipmentType = this.equipmentType;  // copy reference ปกติ (ถ้าเป็น enum ก็ปลอดภัย)
        copy.equipment = this.equipment; // ไม่สร้างใหม่ ใช้ตัวเดิมเลย
        return copy;
    }


    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @JsonIgnore
    public boolean isOccupied() {
        return equipment != null;
    }
}
