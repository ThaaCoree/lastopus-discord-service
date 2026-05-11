package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusType {
    STRENGTH,
    AGILITY,
    VITALITY,
    DEXTERITY,
    WISDOM,
    INTELLIGENCE,
    LUCK;

    public String writeAsString() {
        switch (this) {
            case STRENGTH: return "Strength";
            case AGILITY: return "Agility";
            case VITALITY: return "Vitality";
            case DEXTERITY: return "Dexterity";
            case WISDOM: return "Wisdom";
            case INTELLIGENCE: return "Intelligence";
            case LUCK: return "Luck";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
