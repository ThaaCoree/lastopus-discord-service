package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RecoveryType {
    HEALTH("Health Recovery"),
    MANA("Mana Recovery");

    private final String displayName;

    RecoveryType(String displayName) {
        this.displayName = displayName;
    }

    public String writeAsString() {
        return displayName;
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
