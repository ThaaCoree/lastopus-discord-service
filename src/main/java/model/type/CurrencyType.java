package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyType {
    COPPER("Copper"),
    SILVER("Silver"),
    GOLD("Gold"),
    PLATINUM("Platinum");

    private final String displayName;

    CurrencyType(String displayName) {
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
