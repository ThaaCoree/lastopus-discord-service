package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UnitType {
    PLAYER,
    NPC,
    MONSTER,
    SUMMON;

    public String writeAsString() {
        switch (this) {
            case PLAYER: return "Player";
            case NPC: return "NPC";
            case MONSTER: return "Monster";
            case SUMMON: return "Summon";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
