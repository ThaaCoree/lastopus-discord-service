package main.java.model.entity.summon_scales;

import main.java.model.entity.units.Summon;
import main.java.model.entity.units.Unit;
import main.java.model.modifier.ModValue;
import main.java.model.type.StatType;

import java.util.HashMap;
import java.util.Map;

public class Brave_Emperor extends Summon {

    public Brave_Emperor(Unit owner) {
        super(owner);
        setName("Brave Emperor");
    }

    public Brave_Emperor() {
        super();
    }

    @Override
    public void calculateEverything() {
        if (getOwner() == null) return;
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

        toReturn.get(StatType.PHYSICALATTACK).setCurrent(1.5*patk + 0.8*matk);
        toReturn.get(StatType.MAGICALATTACK).setCurrent(1.6*matk + 0.7*patk);
        toReturn.get(StatType.RANGEDATTACK).setCurrent(1*patk + 1*matk);
        toReturn.get(StatType.HEALTHPOINT).setCurrent(1.6*hp + 0.3*mp);
        toReturn.get(StatType.MANAPOINT).setCurrent(0);
        toReturn.get(StatType.PHYSICALDEFENSE).setCurrent(1.4*pdef + 0.4*patk);
        toReturn.get(StatType.MAGICALDEFENSE).setCurrent(1.4*mdef + 0.4*matk);
        toReturn.get(StatType.MOVEMENTSPEED).setCurrent(1.3*movespeed + 0.2*speed);
        toReturn.get(StatType.HEALAMPLIFIER).setCurrent(1*healAmp);
        toReturn.get(StatType.BUFFAMPLIFIER).setCurrent(1*buffAmp);
        toReturn.get(StatType.DEBUFFAMPLIFIER).setCurrent(1*debuffAmp);
        toReturn.get(StatType.CRITCHANCE).setCurrent(0.7*critChance);
        toReturn.get(StatType.CRITDAMAGE).setCurrent(0.8*critDmg);
        toReturn.get(StatType.MANAREGEN).setCurrent(0);
        toReturn.get(StatType.ACCURACY).setCurrent(2.2*accuracy);
        toReturn.get(StatType.EVASION).setCurrent(1.6*evasion);
        toReturn.get(StatType.PHYSICALBLOCK).setCurrent(2*pBlock);
        toReturn.get(StatType.MAGICALBLOCK).setCurrent(2*mBlock);
        toReturn.get(StatType.DAMAGEREDUCTION).setCurrent(2*dmgReduce);
        toReturn.get(StatType.DAMAGEAMPLIFIER).setCurrent(2*dmgAmp);
        toReturn.get(StatType.ATTACKSPEED).setCurrent(2*atkSpeed);
        toReturn.get(StatType.CASTSPEED).setCurrent(2*castSpeed);
        toReturn.get(StatType.PHYSICALPENETRATE).setCurrent(2.4*pPen);
        toReturn.get(StatType.MAGICALPENETRATE).setCurrent(2.4*mPen);
        toReturn.get(StatType.RESERVATION).setCurrent(1);
        toReturn.get(StatType.CRITSHIELD).setCurrent(1.3*critShield);
        toReturn.get(StatType.SPEED).setCurrent(1*speed);
        toReturn.get(StatType.HEALTHREGEN).setCurrent(0);
        toReturn.get(StatType.DEBUFFRESISTANCE).setCurrent(0);
        toReturn.get(StatType.SOULPOINT).setCurrent(0);

        for (StatType type : StatType.values()) {
            toReturn.get(type).resetFinalToCurrent();
        }

        return toReturn;
    }
}
