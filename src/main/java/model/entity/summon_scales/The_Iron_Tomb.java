package main.java.model.entity.summon_scales;

import main.java.model.entity.units.Summon;
import main.java.model.entity.units.Unit;
import main.java.model.modifier.ModValue;
import main.java.model.type.StatType;

import java.util.HashMap;
import java.util.Map;

public class The_Iron_Tomb extends Summon {

    public The_Iron_Tomb(Unit owner) {
        super(owner);
        setName("The Iron Tomb");
    }

    public The_Iron_Tomb() {
        super();
    }

    @Override
    public void calculateEverything() {
        setLevel(getOwner().getLevel());
        setRemainingStatusPoint(0);
        setStatuses(getOwner().getStatuses());
        setStats(calculateStats());
        reloadSkill();
        calculateSkillDesc();
        getResourceManager().updateMax();
        getStatCalculator().applyConditionsStatModifier();
    }

    public Map<StatType, ModValue> calculateStats() {
        Map<StatType, ModValue> toReturn = new HashMap<>();
        for (StatType type : StatType.values()) {
            toReturn.put(type, new ModValue());
        }
        Map<StatType, ModValue> owners = getOwner().getStats();
        double patk = owners.get(StatType.PHYSICALATTACK).getFinal();
        double matk = owners.get(StatType.MAGICALATTACK).getFinal();
        double ratk = owners.get(StatType.RANGEDATTACK).getFinal();
        double hp = owners.get(StatType.HEALTHPOINT).getFinal();
        double mp = owners.get(StatType.MANAPOINT).getFinal();
        double pdef = owners.get(StatType.PHYSICALDEFENSE).getFinal();
        double mdef = owners.get(StatType.MAGICALDEFENSE).getFinal();
        double movespeed = owners.get(StatType.MOVEMENTSPEED).getFinal();
        double healAmp = owners.get(StatType.HEALAMPLIFIER).getFinal();
        double buffAmp = owners.get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAmp = owners.get(StatType.DEBUFFAMPLIFIER).getFinal();
        double critChance = owners.get(StatType.CRITCHANCE).getFinal();
        double critDmg = owners.get(StatType.CRITDAMAGE).getFinal();
        double mpRegen = owners.get(StatType.MANAREGEN).getFinal();
        double accuracy = owners.get(StatType.ACCURACY).getFinal();
        double evasion = owners.get(StatType.EVASION).getFinal();
        double pBlock = owners.get(StatType.PHYSICALBLOCK).getFinal();
        double mBlock = owners.get(StatType.MAGICALBLOCK).getFinal();
        double dmgReduce = owners.get(StatType.DAMAGEREDUCTION).getFinal();
        double dmgAmp = owners.get(StatType.DAMAGEAMPLIFIER).getFinal();
        double atkSpeed = owners.get(StatType.ATTACKSPEED).getFinal();
        double castSpeed = owners.get(StatType.CASTSPEED).getFinal();
        double pPen = owners.get(StatType.PHYSICALPENETRATE).getFinal();
        double mPen = owners.get(StatType.MAGICALPENETRATE).getFinal();
        double reservation = owners.get(StatType.RESERVATION).getFinal();
        double critShield = owners.get(StatType.CRITSHIELD).getFinal();
        double speed = owners.get(StatType.SPEED).getFinal();
        double hpRegen = owners.get(StatType.HEALTHREGEN).getFinal();
        double debuffRes = owners.get(StatType.DEBUFFRESISTANCE).getFinal();

        toReturn.get(StatType.PHYSICALATTACK).setCurrent(patk);
        toReturn.get(StatType.MAGICALATTACK).setCurrent(matk);
        toReturn.get(StatType.RANGEDATTACK).setCurrent(ratk);
        toReturn.get(StatType.HEALTHPOINT).setCurrent(hp);
        toReturn.get(StatType.MANAPOINT).setCurrent(mp);
        toReturn.get(StatType.PHYSICALDEFENSE).setCurrent(pdef);
        toReturn.get(StatType.MAGICALDEFENSE).setCurrent(mdef);
        toReturn.get(StatType.MOVEMENTSPEED).setCurrent(movespeed);
        toReturn.get(StatType.HEALAMPLIFIER).setCurrent(healAmp);
        toReturn.get(StatType.BUFFAMPLIFIER).setCurrent(buffAmp);
        toReturn.get(StatType.DEBUFFAMPLIFIER).setCurrent(debuffAmp);
        toReturn.get(StatType.CRITCHANCE).setCurrent(critChance);
        toReturn.get(StatType.CRITDAMAGE).setCurrent(critDmg);
        toReturn.get(StatType.MANAREGEN).setCurrent(mpRegen);
        toReturn.get(StatType.ACCURACY).setCurrent(accuracy);
        toReturn.get(StatType.EVASION).setCurrent(evasion);
        toReturn.get(StatType.PHYSICALBLOCK).setCurrent(pBlock);
        toReturn.get(StatType.MAGICALBLOCK).setCurrent(mBlock);
        toReturn.get(StatType.DAMAGEREDUCTION).setCurrent(dmgReduce);
        toReturn.get(StatType.DAMAGEAMPLIFIER).setCurrent(dmgAmp);
        toReturn.get(StatType.ATTACKSPEED).setCurrent(atkSpeed);
        toReturn.get(StatType.CASTSPEED).setCurrent(castSpeed);
        toReturn.get(StatType.PHYSICALPENETRATE).setCurrent(pPen);
        toReturn.get(StatType.MAGICALPENETRATE).setCurrent(mPen);
        toReturn.get(StatType.RESERVATION).setCurrent(reservation);
        toReturn.get(StatType.CRITSHIELD).setCurrent(critShield);
        toReturn.get(StatType.SPEED).setCurrent(speed);
        toReturn.get(StatType.HEALTHREGEN).setCurrent(hpRegen);
        toReturn.get(StatType.DEBUFFRESISTANCE).setCurrent(debuffRes);
        toReturn.get(StatType.SOULPOINT).setCurrent(0);

        for (StatType type : StatType.values()) {
            toReturn.get(type).resetFinalToCurrent();
        }

        return toReturn;
    }
}
