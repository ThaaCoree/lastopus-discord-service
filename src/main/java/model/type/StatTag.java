package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatTag {
    STATUS("Status"),
    STAT("Stat"),
    PHYSICAL("Physical"),
    SPEED("Speed"),
    DEFENSE("Defense"),
    ACCURATE("Accurate"),
    CONTROL("Control"),
    MAGIC("Magic"),
    CRIT("Crit"),
    STRIKE("Strike"),
    AMPLIFIER("Amplifier"),
    RECOVERY("Recovery"),
    REGEN("Regen"),
    RESOURCE("Resource"),
    BLOCK("Block"),
    PENETRATION("Penetration"),
    FIRE("Fire"),
    WATER("Water"),
    WIND("Wind"),
    EARTH("Earth"),
    LIGHT("Light"),
    DARK("Dark"),
    NEUTRAL("Neutral"),
    HEALTH("Health"),
    MANA("Mana");

    private final String displayName;

    StatTag(String displayName) {
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
