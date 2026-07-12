package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MaterialRole {
    BASE("Base"),
    BOOST("Boost");

    private final String displayName;

    MaterialRole(String displayName) {
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
