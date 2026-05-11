package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UniqueType {
    HUMAN("Human"),
    LEDA_GRAYWOLF_BONUS("Grey Wolf Bonus"),
    LEDA_GRAYWOLF_OVERHEAT("Grey Wolf Overheat"),
    YASHA_BULL("Bull"),
    ALILUS_LINK_HOLDER("Link Holder"),
    TWELVE_BLUE_WHALE("Blue Whale"),
    TWILIGHT_INFUSION("Twilight Infusion"),
    TWILIGHT_INFUSION_LUCK("Twilight Infusion Luck"),
    FAIRY("Fairy"),
    ORC("Orc"),
    ELF("Elf"),
    LIGHT_WEIGHT("Light Weight"),
    WINEL_WOLF("Wolf"),
    STARS_DAMNATION("Star's Damnation");

    private final String displayName;

    UniqueType(String displayName) {
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
