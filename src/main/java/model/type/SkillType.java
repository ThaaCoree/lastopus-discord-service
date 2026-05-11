package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum SkillType {
    PHYSICAL,
    SPELL,
    STRIKE,
    FIRE,
    WATER,
    WIND,
    EARTH,
    LIGHT,
    DARK,
    ELEMENTAL,
    HEALING,
    BUFF,
    DEBUFF,
    AOE,      // Area of Effect
    SINGLE_TARGET,
    SUMMON,
    TIME,
    TURN,
    CEREMONY,
    RECOVERY,
    OPUS,
    RACE,
    RESOURCE,
    MOVEMENT,
    FIGHTING_STYLE,
    POISON,
    SCALING,
    CONNECTION,
    REDEMPTION,
    CHANCE,
    DEFENSE,
    MARK,
    CRITICAL,
    IMPRISON,
    STEALTH,
    ETHEREAL,
    KINETIC,
    IGNITE,
    RITUAL,
    DIMENSIONAL,
    FIELD,
    SHADOW,
    LIMIT,
    DRAWBACK,
    DURATION,
    DISTANCE,
    COMBO,
    COUNT,
    REQUIREMENT,
    PURE,
    DISPEL,
    DEBRIS;

    public String writeAsString() {
        String name = this.name();
        String[] parts = name.split("_");
        return Arrays.stream(parts)
                .map(part -> part.charAt(0) + part.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
