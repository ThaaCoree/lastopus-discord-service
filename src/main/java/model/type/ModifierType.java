package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModifierType {
    FLAT("Flat"),
    GLOBAL("Global"),
    EQUIPMENT("Equipment"),
    PASSIVE("Passive"),
    OVERRIDE("Override");

    private final String displayName;

    ModifierType(String displayName) {
        this.displayName = displayName;
    }

    public String writeAsString() {
        return displayName;
    }

    @JsonValue
    public String toValue() {
        return name().toUpperCase();
    }
}
