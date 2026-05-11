package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceType {
    HEALTH,
    MANA,
    DEBRIS;

    public String writeAsString() {
        switch (this) {
            case MANA: return "Mana";
            case HEALTH: return "Health";
            case DEBRIS: return "Debris";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
