package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CardType {
    PRIMARY,
    SECONDARY;

    public String writeAsString() {
        switch (this) {
            case PRIMARY: return "Primary";
            case SECONDARY: return "Secondary";
            default: return name();
        }
    }

    @JsonValue
    public String toValue() {
        return name().toUpperCase();
    }
}
