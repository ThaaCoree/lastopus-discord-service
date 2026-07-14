package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.EnumSet;

public enum StatType {
    PHYSICALATTACK(1, 4, 1.4, 0.7, 1.3,
            StatTag.STAT,
            StatTag.PHYSICAL,
            StatTag.STRIKE),
    MAGICALATTACK(1, 4, 1.4, 0.7, 1.3,
            StatTag.STAT,
            StatTag.MAGIC,
            StatTag.STRIKE,
            StatTag.CONTROL),
    RANGEDATTACK(1, 4, 1.4, 0.7, 1.3,
            StatTag.STAT,
            StatTag.PHYSICAL,
            StatTag.STRIKE,
            StatTag.ACCURATE),
    HEALTHPOINT(6, 21, 1.3, 0.8, 1.2,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.HEALTH,
            StatTag.RESOURCE),
    MANAPOINT(0.5, 0.3, 1.05, 0.7, 1.1,
            StatTag.STAT,
            StatTag.CONTROL,
            StatTag.MANA,
            StatTag.RESOURCE),
    PHYSICALDEFENSE(6, 14, 1.3, 0.9, 1.1,
            StatTag.STAT,
            StatTag.PHYSICAL,
            StatTag.DEFENSE),
    MAGICALDEFENSE(6, 14, 1.3, 0.9, 1.1,
            StatTag.STAT,
            StatTag.MAGIC,
            StatTag.DEFENSE),
    MOVEMENTSPEED(1, 0.35, 1.1, 0.9, 1.2,
            StatTag.STAT,
            StatTag.SPEED),
    HEALAMPLIFIER(0.02, 0.004, 1.05, 0.9, 1.2,
            StatTag.STAT,
            StatTag.MAGIC,
            StatTag.AMPLIFIER,
            StatTag.RECOVERY,
            StatTag.CONTROL),
    BUFFAMPLIFIER(0.01, 0.002, 1.05, 0.9, 1.2,
            StatTag.STAT,
            StatTag.MAGIC,
            StatTag.AMPLIFIER,
            StatTag.CONTROL),
    DEBUFFAMPLIFIER(0.01, 0.002, 1.05, 0.9, 1.2,
            StatTag.STAT,
            StatTag.MAGIC,
            StatTag.AMPLIFIER,
            StatTag.CONTROL),
    CRITCHANCE(0.002, 0.00075, 1.1, 0.8, 1.4,
            StatTag.STAT,
            StatTag.CRIT),
    CRITDAMAGE(0.03, 0.01, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.CRIT,
            StatTag.STRIKE),
    MANAREGEN(0.1, 0.05, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.REGEN,
            StatTag.CONTROL,
            StatTag.RECOVERY,
            StatTag.MANA,
            StatTag.RESOURCE),
    HEALTHREGEN(3, 4.5, 1.2, 0.8, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.RECOVERY,
            StatTag.HEALTH,
            StatTag.RESOURCE),
    DEFLECTION(10, 15, 1.3, 0.9, 1.2,
            StatTag.ACCURATE,
            StatTag.DEFENSE),
    IGNITEAMP(0.01, 0.002, 1.05, 0.9, 1.2,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.FIRE,
            StatTag.AMPLIFIER),
    BLEEDAMP(0.01, 0.002, 1.05, 0.9, 1.2,
            StatTag.STAT,
            StatTag.PHYSICAL,
            StatTag.STRIKE,
            StatTag.AMPLIFIER),
    ACCURACY(7, 10, 1.25, 0.9, 1.2,
            StatTag.STAT,
            StatTag.ACCURATE,
            StatTag.STRIKE),
    EVASION(8, 12, 1.25, 0.9, 1.2,
            StatTag.STAT,
            StatTag.ACCURATE,
            StatTag.DEFENSE),
    PHYSICALBLOCK(2, 2, 1.2, 0.7, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.PHYSICAL,
            StatTag.BLOCK),
    MAGICALBLOCK(2, 2, 1.2, 0.7, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.MAGIC,
            StatTag.BLOCK),
    DAMAGEREDUCTION(0.01, 0.002, 1.4, 0.7, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE),
    DAMAGEAMPLIFIER(0.01, 0.002, 1.4, 0.7, 1.3,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.AMPLIFIER),
    ATTACKSPEED(0.01, 0.0045, 1.4, 0.7, 1.4,
            StatTag.STAT,
            StatTag.SPEED,
            StatTag.STRIKE,
            StatTag.PHYSICAL,
            StatTag.ACCURATE),
    CASTSPEED(0.01, 0.0045, 1.4, 0.7, 1.4,
            StatTag.STAT,
            StatTag.SPEED,
            StatTag.STRIKE,
            StatTag.MAGIC,
            StatTag.CONTROL),
    PHYSICALPENETRATE(5, 11, 1.25, 0.8, 1.2,
            StatTag.STAT,
            StatTag.PHYSICAL,
            StatTag.ACCURATE,
            StatTag.PENETRATION),
    MAGICALPENETRATE(5, 11, 1.25, 0.8, 1.2,
            StatTag.STAT,
            StatTag.MAGIC,
            StatTag.CONTROL,
            StatTag.PENETRATION),
    RESERVATION(-0.01, -0.00075, 1.1, 0.6, 1.3,
            StatTag.STAT,
            StatTag.CONTROL),
    CRITSHIELD(0.001, 0.0004, 1.15, 0.8, 1.4,
            StatTag.STAT,
            StatTag.CRIT,
            StatTag.DEFENSE),
    SPEED(0.1, 0.15, 1.1, 0.5, 1.3,
            StatTag.STAT,
            StatTag.SPEED),
    SOULPOINT(0, 1, 1, 1, 1,
            StatTag.STAT),
    IGNOREPDEF(0.01, 0.001, 1.1, 0.9, 1.1,
            StatTag.STAT,
            StatTag.PHYSICAL,
            StatTag.ACCURATE,
            StatTag.PENETRATION),
    IGNOREMDEF(0.01, 0.001, 1.1, 0.9, 1.1,
            StatTag.STAT,
            StatTag.MAGIC,
            StatTag.CONTROL,
            StatTag.PENETRATION),
    DEBUFFRESISTANCE(0.01, 0.001, 1.1, 0.9, 1.1,
            StatTag.STAT,
            StatTag.DEFENSE),
    POISONAMP(0.01, 0.002, 1.05, 0.9, 1.2,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.ACCURATE,
            StatTag.AMPLIFIER),
    FIREPENETRATION(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.FIRE),
    WATERPENETRATION(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.WATER),
    WINDPENETRATION(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.WIND),
    EARTHPENETRATION(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.EARTH),
    LIGHTPENETRATION(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.LIGHT),
    DARKPENETRATION(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.STRIKE,
            StatTag.DARK),
    FIRERESISTANCE(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.FIRE),
    WATERRESISTANCE(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.WATER),
    WINDRESISTANCE(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.WIND),
    EARTHRESISTANCE(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.EARTH),
    LIGHTRESISTANCE(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.LIGHT),
    DARKRESISTANCE(0.02, 0.0075, 1.1, 0.8, 1.3,
            StatTag.STAT,
            StatTag.DEFENSE,
            StatTag.DARK);

    private final EnumSet<StatTag> tags;
    private final double base_value;
    private final double growth;
    private final double exponent;
    private final double min_multiplier;
    private final double max_multiplier;

    StatType(double base_value, double growth, double exponent, double min_multiplier, double max_multiplier, StatTag... tags) {
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
            case PHYSICALATTACK: return "PhysicalATK";
            case MAGICALATTACK: return "MagicalATK";
            case RANGEDATTACK: return "RangedATK";
            case HEALTHPOINT: return "Health";
            case MANAPOINT: return "Mana";
            case PHYSICALDEFENSE: return "PhysicalDEF";
            case MAGICALDEFENSE: return "MagicalDEF";
            case MOVEMENTSPEED: return "MovementSPD";
            case HEALAMPLIFIER: return "HealAMP";
            case BUFFAMPLIFIER: return "BuffAMP";
            case DEBUFFAMPLIFIER: return "DebuffAMP";
            case CRITCHANCE: return "CritChance";
            case CRITDAMAGE: return "CritDamage";
            case MANAREGEN: return "ManaRegen";
            case ACCURACY: return "Accuracy";
            case EVASION: return "Evasion";
            case PHYSICALBLOCK: return "PhysicalBlock";
            case MAGICALBLOCK: return "MagicalBlock";
            case DAMAGEREDUCTION: return "DamageReduction";
            case DAMAGEAMPLIFIER: return "DamageAmplifier";
            case ATTACKSPEED: return "AttackSpeed";
            case CASTSPEED: return "CastSpeed";
            case PHYSICALPENETRATE: return "PhysicalPenetrate";
            case MAGICALPENETRATE: return "MagicalPenetrate";
            case RESERVATION: return "Reservation";
            case CRITSHIELD: return "CritShield";
            case SPEED: return "Speed";
            case HEALTHREGEN: return "HealthRegen";
            case POISONAMP: return "PoisonAMP";
            case IGNITEAMP: return "IgniteAMP";
            case BLEEDAMP: return "BleedAMP";
            case SOULPOINT: return "Soul Point";
            case IGNOREMDEF: return "Ignore MDEF";
            case IGNOREPDEF: return "Ignore PDEF";
            case DEBUFFRESISTANCE: return "Debuff Resistance";
            case DEFLECTION: return "Deflection";
            default: return name(); // fallback เผื่อ enum มีค่าที่ไม่อยู่ใน switch
        }
    }



    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
