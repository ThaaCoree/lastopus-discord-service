package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CityName {
    RIVEIL("Riveil"),
    VENUS("Venus"),
    KENERIS("Keneris"),
    NARISUA("Narisua"),
    KERENTHES("Kerenthes");

    private final String displayName;

    CityName(String displayName) {
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
