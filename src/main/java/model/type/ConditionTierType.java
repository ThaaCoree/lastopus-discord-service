package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConditionTierType {
    BASIC("Basic"),
    GENERAL("General"),
    ADVANCED("Advanced"),
    BOUND("Bound"),
    UNDISPELLABLE("Undispellable");

    private final String displayName;

    ConditionTierType(String displayName) {
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
