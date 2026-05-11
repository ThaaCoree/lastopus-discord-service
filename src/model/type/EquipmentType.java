package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EquipmentType {
    HELMET,
    ARMOR,
    BOOTS,
    GLOVES,
    ACCESSORY,
    WEAPON,
    BACKPACK;

    public String writeAsString() {
        switch (this) {
            case HELMET: return "Helmet";
            case ARMOR: return "Armor";
            case BOOTS: return "Boots";
            case GLOVES: return "Gloves";
            case ACCESSORY: return "Accessory";
            case WEAPON: return "Weapon";
            case BACKPACK: return "Backpack";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
