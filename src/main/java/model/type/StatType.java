package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatType {
    PHYSICALATTACK,
    MAGICALATTACK,
    RANGEDATTACK,
    HEALTHPOINT,
    MANAPOINT,
    PHYSICALDEFENSE,
    MAGICALDEFENSE,
    MOVEMENTSPEED,
    HEALAMPLIFIER,
    BUFFAMPLIFIER,
    DEBUFFAMPLIFIER,
    CRITCHANCE,
    CRITDAMAGE,
    MANAREGEN,
    HEALTHREGEN,
    POISONAMP,
    IGNITEAMP,
    BLEEDAMP,
    ACCURACY,
    EVASION,
    PHYSICALBLOCK,
    MAGICALBLOCK,
    DAMAGEREDUCTION,
    DAMAGEAMPLIFIER,
    ATTACKSPEED,
    CASTSPEED,
    PHYSICALPENETRATE,
    MAGICALPENETRATE,
    RESERVATION,
    CRITSHIELD,
    SPEED,
    SOULPOINT,
    IGNOREPDEF,
    IGNOREMDEF,
    DEBUFFRESISTANCE;

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
            default: return name(); // fallback เผื่อ enum มีค่าที่ไม่อยู่ใน switch
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
