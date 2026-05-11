package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DamageType {
    PHYSICAL,
    MAGICAL,
    PURE,
    TRUE;

    public String writeAsString() {
        switch (this) {
            case PHYSICAL: return "Physical";
            case MAGICAL: return "Magical";
            case PURE: return "Pure";
            case TRUE: return "True";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}