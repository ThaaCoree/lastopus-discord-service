package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConditionType {
    BUFF("Buff"),
    DEBUFF("Debuff"),
    NEUTRAL("Neutral");

    private final String displayName;

    ConditionType(String displayName) {
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
