package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.EnumSet;

public enum StatusType {
    STRENGTH(1, 1.5, 1.25, 0.9, 1.2,
            StatTag.STATUS,
            StatTag.PHYSICAL),
    AGILITY(1, 1.5, 1.25, 0.9, 1.2,
            StatTag.STATUS,
            StatTag.SPEED),
    VITALITY(1, 1.5, 1.25, 0.9, 1.2,
            StatTag.STATUS,
            StatTag.DEFENSE),
    DEXTERITY(1, 1.5, 1.25, 0.9, 1.2,
            StatTag.STATUS,
            StatTag.ACCURATE),
    WISDOM(1, 1.5, 1.25, 0.9, 1.2,
            StatTag.STATUS,
            StatTag.CONTROL),
    INTELLIGENCE(1, 1.5, 1.25, 0.9, 1.2,
            StatTag.STATUS,
            StatTag.MAGIC),
    LUCK(1, 1.25, 1.25, 0.9, 1.2,
            StatTag.STATUS,
            StatTag.CRIT);

    private final EnumSet<StatTag> tags;
    private final double base_value;
    private final double growth;
    private final double exponent;
    private final double min_multiplier;
    private final double max_multiplier;

    StatusType(double base_value, double growth, double exponent, double min_multiplier, double max_multiplier, StatTag... tags) {
        this.tags = EnumSet.noneOf(StatTag.class);
        Collections.addAll(this.tags, tags);
        this.base_value = base_value;
        this.growth = growth;
        this.exponent = exponent;
        this.min_multiplier = min_multiplier;
        this.max_multiplier = max_multiplier;
    }

    public EnumSet<StatTag> getTags() {
        return EnumSet.copyOf(tags);
    }

    public double getBase_value() {
        return base_value;
    }

    public double getGrowth() {
        return growth;
    }

    public double getExponent() {
        return exponent;
    }

    public double getMin_multiplier() {
        return min_multiplier;
    }

    public double getMax_multiplier() {
        return max_multiplier;
    }

    public boolean hasTag(StatTag tag) {
        return tags.contains(tag);
    }

    public String writeAsString() {
        switch (this) {
            case STRENGTH: return "Strength";
            case AGILITY: return "Agility";
            case VITALITY: return "Vitality";
            case DEXTERITY: return "Dexterity";
            case WISDOM: return "Wisdom";
            case INTELLIGENCE: return "Intelligence";
            case LUCK: return "Luck";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
