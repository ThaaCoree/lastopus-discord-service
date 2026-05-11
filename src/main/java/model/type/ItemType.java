package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemType {
    NONE,
    CONSUMABLE,
    MATERIAL,
    EQUIPMENT,
    HERB,
    FOOD,
    TOOL,
    FOOD_INGREDIENT,
    ORE,
    DREAM,
    RUNE;

    public String writeAsString() {
        switch (this) {
            case NONE: return "";
            case CONSUMABLE: return "Consumable";
            case MATERIAL: return "Material";
            case EQUIPMENT: return "Equipment";
            case HERB: return "Herb";
            case FOOD: return "Food";
            case TOOL: return "Tool";
            case FOOD_INGREDIENT: return "Food Ingredient";
            case ORE: return "Ore";
            case DREAM: return "Dream";
            case RUNE: return "Rune";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
