package calculator;

import model.entity.*;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.skills.SkillInstance;
import model.entity.units.Unit;
import model.modifier.BasicModifier;
import model.modifier.ModValue;
import model.modifier.TransferModifier;
import model.type.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatCalculator {

    private final Unit unit;

    public StatCalculator(Unit unit) {
        this.unit = unit;
    }

    public void calculateBaseStatsFromCurrentStatus() {
        int level = unit.getLevel();

        double str = unit.getStatuses().get(StatusType.STRENGTH).getFinal();
        double dex = unit.getStatuses().get(StatusType.DEXTERITY).getFinal();
        double intel = unit.getStatuses().get(StatusType.INTELLIGENCE).getFinal();
        double vit = unit.getStatuses().get(StatusType.VITALITY).getFinal();
        double agi = unit.getStatuses().get(StatusType.AGILITY).getFinal();
        double wis = unit.getStatuses().get(StatusType.WISDOM).getFinal();
        double luk = unit.getStatuses().get(StatusType.LUCK).getFinal();
        
        Map<StatType, ModValue> stats = unit.getStats();

        stats.get(StatType.HEALTHPOINT).setBase(10 + 5 * str + 100 * level + 6 * vit);
        stats.get(StatType.MANAPOINT).setBase(5 + wis*0.3 + level*0.5);
        stats.get(StatType.PHYSICALATTACK).setBase(str * 1);
        stats.get(StatType.MAGICALATTACK).setBase(intel * 1);
        stats.get(StatType.RANGEDATTACK).setBase(dex * 1);
        stats.get(StatType.PHYSICALDEFENSE).setBase(1 + 3.5 * vit + 2 * str);
        stats.get(StatType.MAGICALDEFENSE).setBase(1 + 3.5 * vit + 2 * intel);
        stats.get(StatType.MOVEMENTSPEED).setBase(2 + 0.12 * agi + 0.03 * dex);
        stats.get(StatType.HEALAMPLIFIER).setBase(0.005 * wis);
        stats.get(StatType.BUFFAMPLIFIER).setBase(0.0025 * wis);
        stats.get(StatType.DEBUFFAMPLIFIER).setBase(0.0025 * wis);
        stats.get(StatType.CRITCHANCE).setBase(0.05);
        stats.get(StatType.CRITDAMAGE).setBase(1.5 + 0.015 * luk);
        stats.get(StatType.MANAREGEN).setBase(0.0);
        stats.get(StatType.ACCURACY).setBase(30 + 7 * dex);
        stats.get(StatType.EVASION).setBase(50 + 6 * agi + 2 * dex);
        stats.get(StatType.PHYSICALBLOCK).setBase(10 + 2 * vit - agi);
        stats.get(StatType.MAGICALBLOCK).setBase(10 + 2 * vit - agi);
        stats.get(StatType.DAMAGEAMPLIFIER).setBase(0.0);
        stats.get(StatType.DAMAGEREDUCTION).setBase(0.0);
        stats.get(StatType.ATTACKSPEED).setBase(1 + 0.006 * agi);
        stats.get(StatType.CASTSPEED).setBase(1 + 0.006 * agi);
        stats.get(StatType.PHYSICALPENETRATE).setBase(1 + 1.8 * str + 1.4 * luk);
        stats.get(StatType.MAGICALPENETRATE).setBase(1 + 1.8 * intel + 1.4 * luk);
        stats.get(StatType.RESERVATION).setBase(1 / (1 + 0.01 * wis));
        stats.get(StatType.CRITSHIELD).setBase(0.025);
        stats.get(StatType.SPEED).setBase(1 + 0.1 * agi);
        stats.get(StatType.SOULPOINT).setBase(100);
        stats.get(StatType.DEBUFFRESISTANCE).setBase(0);

        if (unit.getName() != null) {
            if (unit.getName().equals("Four-Leaf Clover777")) {
                stats.get(StatType.HEALTHPOINT).setBase(10 + luk*0.85 + 100*level);
                stats.get(StatType.MANAPOINT).setBase(5 + luk*0.05 + 0.5*level);
                stats.get(StatType.PHYSICALATTACK).setBase(luk * 0.44);
                stats.get(StatType.MAGICALATTACK).setBase(luk * 0.44);
                stats.get(StatType.RANGEDATTACK).setBase(luk * 0.44);
                stats.get(StatType.PHYSICALDEFENSE).setBase(1 + luk * 0.3);
                stats.get(StatType.MAGICALDEFENSE).setBase(1 + luk * 0.3);
                stats.get(StatType.MOVEMENTSPEED).setBase(2 + luk * 0.018);
                stats.get(StatType.HEALAMPLIFIER).setBase(0.001 * luk);
                stats.get(StatType.BUFFAMPLIFIER).setBase(0.0005 * luk);
                stats.get(StatType.DEBUFFAMPLIFIER).setBase(0.0005 * luk);
                stats.get(StatType.CRITDAMAGE).setBase(1.5 + 0.003 * luk);
                stats.get(StatType.ACCURACY).setBase(30 + 0.9 * luk);
                stats.get(StatType.EVASION).setBase(50 + 1.1 * luk);
                stats.get(StatType.PHYSICALBLOCK).setBase(10 + luk * 0.5);
                stats.get(StatType.MAGICALBLOCK).setBase(10 + luk * 0.5);
                stats.get(StatType.ATTACKSPEED).setBase(1 + 0.002 * luk);
                stats.get(StatType.CASTSPEED).setBase(1 + 0.002 * luk);
                stats.get(StatType.PHYSICALPENETRATE).setBase(1 + 0.8 * luk);
                stats.get(StatType.MAGICALPENETRATE).setBase(1 + 0.8 * luk);
                stats.get(StatType.RESERVATION).setBase(1 / (1 + 0.002 * luk));
                stats.get(StatType.SPEED).setBase(1 + 0.02 * luk);
            }
        }

        for (StatType type : StatType.values()) {
            if (stats.get(type) != null) {
                stats.get(type).setCurrent(stats.get(type).getBase());
            } else {
                ModValue modifier = new ModValue();
                modifier.setBase(0);
                modifier.setCurrent(0);
                modifier.setFinal(0);
                stats.put(type,modifier);
            }
        }
        for (StatType type : StatType.values()) {
            stats.get(type).setFinal(stats.get(type).getBase());
        }
    }

    public void calculateManaRegen() {
        double mp = unit.getStats().get(StatType.MANAPOINT).getCurrent();
        double wis = unit.getStatuses().get(StatusType.WISDOM).getCurrent();
        double intel = unit.getStatuses().get(StatusType.INTELLIGENCE).getCurrent();
        double luk = unit.getStatuses().get(StatusType.LUCK).getCurrent();
        if (unit.hasSkill("Hemoniea's Vessel")) {
            if (unit.getAllSkill().get("Hemoniea's Vessel").isReserving()) {
                intel = unit.getStatuses().get(StatusType.VITALITY).getCurrent();
            }
        }
        double manaregen = 1;
        manaregen += (mp*0.05);
        manaregen *= 1+(intel/100);
            unit.getStats().get(StatType.MANAREGEN).setCurrent(manaregen);

        if (unit.getName() != null) {
            manaregen = 1;
            manaregen += (mp*0.05);
            manaregen *= 1+(luk/100);
            if (unit.getName().equals("Four-Leaf Clover777")) {
                unit.getStats().get(StatType.MANAREGEN).sumToCurrent(manaregen);
            }
        }
    }

    public void calculateCritChance() {
        double luk = unit.getStatuses().get(StatusType.LUCK).getFinal();
        double crit_chance = unit.getStats().get(StatType.CRITCHANCE).getCurrent();
        crit_chance *= 1+(0.01*luk);
        double crit_shield = unit.getStats().get(StatType.CRITSHIELD).getCurrent();
        crit_shield *= 1+(0.01*luk);

        unit.getStats().get(StatType.CRITCHANCE).setCurrent(crit_chance);
        unit.getStats().get(StatType.CRITSHIELD).setCurrent(crit_shield);
    }

    public void applyBasicStatModifiers() {

        Map<StatType, Double> equipFlatSum = new HashMap<>();
        Map<StatType, Double> equipMultSum = new HashMap<>();
        Map<StatType, Double> passiveFlatSum = new HashMap<>();
        Map<StatType, Double> passiveMultSum = new HashMap<>();
        Map<StatType, Double> cardFlatSum = new HashMap<>();
        Map<StatType, Double> raceFlatSum = new HashMap<>();
        Map<StatType, Double> skillFlatSum = new HashMap<>();
        Map<StatType, Double> runeFlatSum = new HashMap<>();
        Map<StatType, Double> globalMultProduct = new HashMap<>();

        // เริ่มต้นค่า
        for (StatType type : StatType.values()) {
            equipFlatSum.put(type, 0.0);
            equipMultSum.put(type, 0.0);
            passiveFlatSum.put(type, 0.0);
            passiveMultSum.put(type, 0.0);
            globalMultProduct.put(type, 1.0);
            raceFlatSum.put(type, 0.0);
            skillFlatSum.put(type, 0.0);
            cardFlatSum.put(type, 0.0);
            runeFlatSum.put(type, 0.0);
        }

        for (StatType statType : StatType.values()) {
            for (UniqueModifier modifier : unit.getUniqueModifier()) {
                if (modifier.getModifiers().getStatModifiers().get(statType) != null) {
                    double toAdd = modifier.getModifiers().getStatModifiers().get(statType).getFlat();
                    double toMult = modifier.getModifiers().getStatModifiers().get(statType).getGlobalMult();
                    raceFlatSum.merge(statType, toAdd, Double::sum);
                }
            }

            for (CardType cardType : CardType.values()) {
                if (unit.getCard().get(cardType) == null) continue;
                if (unit.getCard().get(cardType).getStatModifiers(cardType).get(statType) != null) {
                    double cardToAdd = unit.getCard().get(cardType).getStatModifiers(cardType).get(statType).getFlat();
                    double cardToMult = unit.getCard().get(cardType).getStatModifiers(cardType).get(statType).getGlobalMult();
                    cardFlatSum.merge(statType, cardToAdd, Double::sum);
                }
            }
        }

        //skills
        for (SkillInstance instance : unit.getAllSkill().values()) {
            for (StatType type : StatType.values()) {
                if (instance.getInstanceBundle().getStatModifiers().get(type) == null) continue;
                double skillToAdd = instance.getInstanceBundle().getStatModifiers().get(type).getFlat();
                double skillToMult = instance.getInstanceBundle().getStatModifiers().get(type).getGlobalMult();
                skillFlatSum.merge(type, skillToAdd, Double::sum);
            }
        }

        for (StatType type : StatType.values()) {
            if (unit.getRune_modifiers().getStatModifiers().get(type) == null) continue;
            double runeToAdd = unit.getRune_modifiers().getStatModifiers().get(type).getFlat();
            skillFlatSum.merge(type, runeToAdd, Double::sum);
        }

        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue;

            double handMultiplier = 1;
            if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                handMultiplier = unit.getMixTwoHandedMult();
            }

            for (StatType type : StatType.values()) {
                BasicModifier modifier = equipment.getStatModifiers().get(type);
                double equipmentMod = 1;
                for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                    Double mult = null;
                    Map<EquipmentType, Double> multMap = entry.getValue().getEquipmentSlotMult();
                    if (multMap != null) {
                        mult = multMap.get(equipment.getEquipmentType());
                    }
                    if (mult != null) {
                        equipmentMod += mult;
                    }
                }
                if (modifier == null) continue;

                equipFlatSum.merge(type, modifier.getFlat() * handMultiplier * equipmentMod, Double::sum);
                equipMultSum.merge(type, modifier.getEquipmentMult() * handMultiplier * equipmentMod, Double::sum);
                passiveMultSum.merge(type, modifier.getPassiveMult() * handMultiplier * equipmentMod, Double::sum);
            }
        }

        for (PassiveNode node : unit.getAllocatedPassives().values()) {
            if (node == null) continue;

            for (StatType type : StatType.values()) {
                BasicModifier modifier = node.getStatModifiers().get(type);
                if (modifier == null) continue;

                passiveFlatSum.merge(type, modifier.getFlat(), Double::sum);
                equipMultSum.merge(type, modifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, modifier.getPassiveMult(), Double::sum);
            }
        }

        for (ConditionInstance instance : unit.getConditionInstances().values()) {
            Conditions condition = instance.getCondition();
            if (condition == null) continue;
            Unit source = instance.getSource();
            double amp = 1;
            if (source != null) {
                if (condition.getConditionType() == ConditionType.BUFF) {
                    amp += source.getStats().get(StatType.BUFFAMPLIFIER).getFinal();
                }
                if (condition.getConditionType() == ConditionType.DEBUFF) {
                    amp += source.getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
                }
            }
            double resist = 1;
            if (condition.getConditionType() == ConditionType.DEBUFF) {
                resist = Math.max(0.2, (1-unit.getStats().get(StatType.DEBUFFRESISTANCE).getFinal()));
            }
            double ia = 1;
            double ua = 1;
            for (PassiveNode node : unit.getAllocatedPassives().values()){
                if (node.getName().equals("Intense Affection")) {
                    ia = 2;
                }
                if (node.getName().equals("Unaffected Affection")) {
                    ua = 0;
                }
                if (condition.getConditionType().equals(ConditionType.NEUTRAL)) {
                    ia = 1;
                    ua = 1;
                }
            }
            for (StatType type : StatType.values()) {
                BasicModifier modifier = condition.getModifiers().getStatModifiers().get(type);
                if (modifier == null) continue;
                equipMultSum.merge(type, modifier.getEquipmentMult() * amp * ia * ua * resist, Double::sum);
                passiveMultSum.merge(type, modifier.getPassiveMult() * amp * ia * ua * resist, Double::sum);
            }
        }

        for (StatType type : StatType.values()) {
            globalMultProduct.put(type, 1.0);
            double modifier = 0;

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                BasicModifier basicModifier = node.getStatModifiers().get(type);
                if (basicModifier == null) continue;
                equipMultSum.merge(type, basicModifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, basicModifier.getPassiveMult(), Double::sum);
            }
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                if (slot.getEquipment() == null) continue;
                BasicModifier basicModifier = slot.getEquipment().getStatModifiers().get(type);
                if (basicModifier == null) continue;
                equipMultSum.merge(type, basicModifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, basicModifier.getPassiveMult(), Double::sum);
            }

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                if (node.getStatModifiers().get(type) == null) continue;
                modifier = node.getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type,(modifier*(1+passiveMultSum.get(type))+1), (oldVal, newVal) -> oldVal * newVal);
            }
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                Equipment equipment = slot.getEquipment();
                if (equipment == null) continue;

                double handMultiplier = 1;
                if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                    handMultiplier = unit.getMixTwoHandedMult();
                }
                double equipmentMod = 1;
                for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                    Double mult = null;
                    Map<EquipmentType, Double> multMap = entry.getValue().getEquipmentSlotMult();
                    if (multMap != null) {
                        mult = multMap.get(equipment.getEquipmentType());
                    }
                    if (mult != null) {
                        equipmentMod += mult;
                    }
                }
                if (slot.getEquipment().getStatModifiers().get(type) == null) continue;
                modifier = slot.getEquipment().getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type, ((modifier * handMultiplier * equipmentMod*(1+equipMultSum.get(type)))+1), (oldVal, newVal) -> oldVal * newVal);
            }
            for (SkillInstance instance : unit.getAllSkill().values()) {
                if (instance.getSkillData() == null) continue;
                if (instance.getSkillData().getSkillModifier().getStatModifiers().get(type) == null) continue;
                modifier = instance.getSkillData().getSkillModifier().getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                if (instance.getInstanceBundle().getStatModifiers().get(type) == null) continue;
                double skillToMult = instance.getInstanceBundle().getStatModifiers().get(type).getGlobalMult();
                globalMultProduct.merge(type, skillToMult+1, (oldVal, newVal) -> oldVal * newVal);
            }
            for (CardType cardType : unit.getCard().keySet()) {
                if (unit.getCard().get(cardType).getStatModifiers(cardType).get(type) == null) continue;
                modifier = unit.getCard().get(cardType).getStatModifiers(cardType).get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
            for (UniqueModifier uniqueModifier : unit.getUniqueModifier()) {
                if (uniqueModifier.getModifiers().getStatModifiers().get(type) == null) continue;
                modifier = uniqueModifier.getModifiers().getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
            if (unit.getRune_modifiers().getStatModifiers().get(type) != null) {
                modifier = unit.getRune_modifiers().getStatModifiers().get(type).getGlobalMult();
                globalMultProduct.merge(type, modifier + 1, (oldVal, newVal) -> oldVal * newVal);
            }
        }

        // Apply Flat + EquipMult + PassiveMult
        for (StatType type : StatType.values()) {
            double equipFlat = equipFlatSum.getOrDefault(type, 0.0);
            double equipMult = equipMultSum.getOrDefault(type, 0.0);
            double passiveFlat = passiveFlatSum.getOrDefault(type, 0.0);
            double passiveMult = passiveMultSum.getOrDefault(type, 0.0);
            double raceFlat = raceFlatSum.getOrDefault(type, 0.0);
            double cardFlat = cardFlatSum.getOrDefault(type, 0.0);
            double skillFlat = skillFlatSum.get(type);
            double runeFlat = runeFlatSum.get(type);
            unit.getStats().get(type).sumToCurrent(equipFlat + passiveFlat);
            unit.getStats().get(type).sumToCurrent(equipFlat * equipMult);
            unit.getStats().get(type).sumToCurrent(passiveFlat * passiveMult);
            unit.getStats().get(type).sumToCurrent(raceFlat);
            unit.getStats().get(type).sumToCurrent(cardFlat);
            unit.getStats().get(type).sumToCurrent(skillFlat);
            unit.getStats().get(type).sumToCurrent(runeFlat);
        }

        //คำนวณ CritChance และ Shield ก่อนที่จะ apply Global Mult
        calculateCritChance();

        // Apply Global Mult
        for (StatType type : StatType.values()) {
            unit.getStats().get(type).multToCurrent(globalMultProduct.getOrDefault(type, 1.0));
        }
    }

    public void applyTransferStatModifier() {
        Map<StatType, Double> stackedConvertPercent = new HashMap<>();
        for (StatType type : StatType.values()) {
            stackedConvertPercent.put(type, 0.0);
        }
        Map<StatType, Double> actualConvert = new HashMap<>();
        Map<StatType, Double> added = new HashMap<>();
        Map<StatType, Double> globalMultProduct = new HashMap<>();
        Map<StatType, Double> passiveMultSum = new HashMap<>();
        Map<StatType, Double> equipMultSum = new HashMap<>();

        for (StatType type : StatType.values()) {
            globalMultProduct.put(type, 1.0);
            BasicModifier modifier = null;

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                modifier = node.getStatModifiers().get(type);
                if (modifier == null) continue;
                equipMultSum.merge(type, modifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, modifier.getPassiveMult(), Double::sum);
            }
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                if (slot.getEquipment() == null) continue;
                modifier = slot.getEquipment().getStatModifiers().get(type);
                if (modifier == null) continue;
                equipMultSum.merge(type, modifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, modifier.getPassiveMult(), Double::sum);
            }
            //rune multSum
            if (unit.getRune_modifiers().getStatModifiers().get(type) != null) {
                modifier = unit.getRune_modifiers().getStatModifiers().get(type);
                globalMultProduct.merge(type, modifier.getGlobalMult()+1, (oldVal, newVal) -> oldVal * newVal);
            }
            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                modifier = node.getStatModifiers().get(type);
                if (modifier == null) continue;
                globalMultProduct.merge(type,(modifier.getGlobalMult()+1)*(1+passiveMultSum.get(type)), (oldVal, newVal) -> oldVal * newVal);
            }
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                Equipment equipment = slot.getEquipment();
                if (equipment == null) continue;

                double handMultiplier = 1;
                if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                    handMultiplier = unit.getMixTwoHandedMult();
                }
                double equipmentMod = 1;
                for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                    Double mult = null;
                    Map<EquipmentType, Double> multMap = entry.getValue().getEquipmentSlotMult();
                    if (multMap != null) {
                        mult = multMap.get(equipment.getEquipmentType());
                    }
                    if (mult != null) {
                        equipmentMod += mult;
                    }
                }
                modifier = slot.getEquipment().getStatModifiers().get(type);
                if (modifier == null) continue;
                globalMultProduct.merge(type, ((modifier.getGlobalMult() * handMultiplier * equipmentMod)+1)*(1+equipMultSum.get(type)), (oldVal, newVal) -> oldVal * newVal);
            }
            for (SkillInstance instance : unit.getAllSkill().values()) {
                if (instance.getSkillData() == null) continue;
                modifier = instance.getSkillData().getSkillModifier().getStatModifiers().get(type);
                if (modifier == null) continue;
                if (instance.getInstanceBundle().getStatModifiers().get(type) == null) continue;
                double skillToMult = instance.getInstanceBundle().getStatModifiers().get(type).getGlobalMult();
                globalMultProduct.merge(type, skillToMult+1, (oldVal, newVal) -> oldVal * newVal);
            }
            for (CardType cardType : unit.getCard().keySet()) {
                modifier = unit.getCard().get(cardType).getStatModifiers(cardType).get(type);
                if (modifier == null) continue;
                globalMultProduct.merge(type,modifier.getGlobalMult()+1, (oldVal,newVal) -> oldVal * newVal);
            }
            for (UniqueModifier uniqueModifier : unit.getUniqueModifier()) {
                modifier = uniqueModifier.getModifiers().getStatModifiers().get(type);
                if (modifier == null) continue;
                globalMultProduct.merge(type,modifier.getGlobalMult()+1, (oldVal,newVal) -> oldVal * newVal);
            }
        }
        
        //convert unique
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getModifiers().getTransferModifiers() != null) {
                calculateConversion(modifier.getModifiers().getTransferModifiers(), stackedConvertPercent, actualConvert, added);
            }
        }
        //convert rune
        if (unit.getRune_modifiers().getTransferModifiers() != null) {
            calculateConversion(unit.getRune_modifiers().getTransferModifiers(), stackedConvertPercent, actualConvert, added);
        }

        //convert skills
        for (SkillInstance instance : unit.getAllSkill().values()) {
            if (instance.getInstanceBundle().getTransferModifiers() == null) continue;
            calculateConversion(instance.getInstanceBundle().getTransferModifiers(), stackedConvertPercent, actualConvert, added);
        }

        //convert passive
        for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
            PassiveNode node = entry.getValue();

            if (node.getTransferModifiers() != null) {
                calculateConversion(node.getTransferModifiers(), stackedConvertPercent, actualConvert, added);
            }
        }
        //convert equipment
        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue;

            double handMultiplier = 1;
            if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                handMultiplier = unit.getMixTwoHandedMult();
            }
            if (equipment.getTransferModifiers() != null) {
                calculateConversion(equipment.getTransferModifiers(), stackedConvertPercent, actualConvert, added, handMultiplier);
            }
        }
        //convert card
        for (Map.Entry<CardType, Card> entry : unit.getCard().entrySet()) {
            CardType type = entry.getKey();
            Card card = entry.getValue();
            if (card.getTransferModifiers(type) != null) {
                calculateConversion(card.getTransferModifiers(type), stackedConvertPercent, actualConvert, added);
            }
        }

        for (Map.Entry<StatType, Double> entry : actualConvert.entrySet()) {
            StatType type = entry.getKey();
            double toMinus = (-1) * entry.getValue();
            unit.getStats().get(type).sumToCurrent(toMinus);
        }
        for (Map.Entry<StatType, Double> entry : added.entrySet()) {
            StatType type = entry.getKey();
            double toAdd = entry.getValue();
            toAdd *= globalMultProduct.get(entry.getKey());
            unit.getStats().get(type).sumToCurrent(toAdd);
        }

        added.clear(); //clear ก่อนไป gain ต่อ เพื่อไม่ให้ transfer added มัน stack

        //gain unique
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getModifiers().getTransferModifiers() != null) {
                calculateGain(modifier.getModifiers().getTransferModifiers(), added);
            }
        }

        //gain rune
        if (unit.getRune_modifiers().getTransferModifiers() != null) {
            calculateGain(unit.getRune_modifiers().getTransferModifiers(), added);
        }

        //gain skills
        for (SkillInstance instance : unit.getAllSkill().values()) {
            if (instance.getInstanceBundle().getTransferModifiers() == null) continue;
            calculateGain(instance.getInstanceBundle().getTransferModifiers(), added);
        }

        //gain passive
        for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
            PassiveNode node = entry.getValue();
            if (node.getTransferModifiers() != null) {
                calculateGain(node.getTransferModifiers(), added);
            }
        }

        //gain weaponPassive
        Set<WeaponType> appliedTypes = new HashSet<>();

        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue; // กัน NPE
            WeaponType weaponType = equipment.getWeaponType();

            if (!appliedTypes.contains(weaponType)) {
                if (weaponType == WeaponType.JAVELIN) {
                    Map<Integer, TransferModifier> transferModifier = new HashMap<>();
                    TransferModifier toPut = new TransferModifier(StatType.PHYSICALATTACK,
                            StatType.RANGEDATTACK,
                            null,
                            null,
                            0.25,
                            1);
                    toPut.setTransferType(TransferType.GAIN);
                    transferModifier.put(1, toPut);
                    calculateGain(transferModifier, added);
                }
                appliedTypes.add(weaponType); // mark ว่าใช้แล้ว
            }
        }

        //gain equipment
        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue;

            double handMultiplier = 1;
            if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                handMultiplier = unit.getMixTwoHandedMult();
            }
            if (equipment.getTransferModifiers() != null) {
                calculateGain(equipment.getTransferModifiers(), added, handMultiplier);
            }
        }
        //gain card
        for (Map.Entry<CardType, Card> entry : unit.getCard().entrySet()) {
            CardType type = entry.getKey();
            Card card = entry.getValue();
            if (card.getTransferModifiers(type) != null) {
                calculateGain(card.getTransferModifiers(type), added);
            }
        }

        for (Map.Entry<StatType, Double> entry : added.entrySet()) {
            StatType type = entry.getKey();
            double toAdd = entry.getValue();
            toAdd *= globalMultProduct.get(entry.getKey());
            unit.getStats().get(type).sumToCurrent(toAdd);
        }
    }

    public void applyConditionsStatModifier() {
        resetFinal();
        if (unit.getConditionInstances().isEmpty()) return;
        Map<StatType, Double> conditionFlatSum = new HashMap<>();
        Map<StatType, Double> conditionMultProduct = new HashMap<>();
        for (StatType type : StatType.values()) {
            conditionFlatSum.put(type, 0.0);
            conditionMultProduct.put(type, 1.0);
        }

        for (ConditionInstance instance : unit.getConditionInstances().values()) {
            Conditions condition = instance.getCondition();
            if (condition == null) continue;
            Unit source = instance.getSource();
            double amp = 1;
            if (source != null) {
                if (condition.getConditionType() == ConditionType.BUFF) {
                    amp += source.getStats().get(StatType.BUFFAMPLIFIER).getFinal();
                }
                if (condition.getConditionType() == ConditionType.DEBUFF) {
                    amp += source.getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
                }
            }
            double resist = 1;
            if (condition.getConditionType() == ConditionType.DEBUFF) {
                resist = Math.max(0.2, (1-unit.getStats().get(StatType.DEBUFFRESISTANCE).getFinal()));
            }
            double ia = 1;
            double ua = 1;
            for (PassiveNode node : unit.getAllocatedPassives().values()){
                if (node.getName().equals("Intense Affection")) {
                    ia = 2;
                }
                if (node.getName().equals("Unaffected Affection")) {
                    ua = 0;
                }
                if (condition.getConditionType().equals(ConditionType.NEUTRAL)) {
                    ia = 1;
                    ua = 1;
                }
            }
            for (StatType type : StatType.values()) {
                BasicModifier modifier = condition.getModifiers().getStatModifiers().get(type);
                if (modifier == null) continue;
                conditionFlatSum.merge(type, modifier.getFlat() * amp * ia * ua * resist, Double::sum);
                conditionMultProduct.merge(type, (modifier.getGlobalMult() * amp * ia * ua * resist)+1, (oldVal, newVal) -> oldVal * newVal);
            }
        }
        // Apply Flat + Global
        for (StatType type : StatType.values()) {
            double conditionFlat = conditionFlatSum.getOrDefault(type, 0.0);
            unit.getStats().get(type).sumToFinal(conditionFlat);
            unit.getStats().get(type).multToFinal(conditionMultProduct.getOrDefault(type, 1.0));
        }

        Map<StatType, Double> actualConvert = new HashMap<>();
        Map<StatType, Double> added = new HashMap<>();

        //convert
        for (ConditionInstance instance : unit.getConditionInstances().values()) {
            Conditions condition = instance.getCondition();
            if (condition == null) continue;
            Unit source = instance.getSource();
            double amp = 1;
            if (source != null) {
                if (condition.getConditionType() == ConditionType.BUFF) {
                    amp += source.getStats().get(StatType.BUFFAMPLIFIER).getFinal();
                }
                if (condition.getConditionType() == ConditionType.DEBUFF) {
                    amp += source.getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
                }
            }
            double ia = 1;
            double ua = 1;
            for (PassiveNode node : unit.getAllocatedPassives().values()){
                if (node.getName().equals("Intense Affection")) {
                    ia = 2;
                }
                if (node.getName().equals("Unaffected Affection")) {
                    ua = 0;
                }
                if (condition.getConditionType().equals(ConditionType.NEUTRAL)) {
                    ia = 1;
                    ua = 1;
                }
            }
            if (condition.getTransferModifiers() != null) {
                for (TransferModifier tm : condition.getTransferModifiers().values()) {
                    if (tm.getSourceStat() == null || tm.getTargetStat() == null) continue;

                    StatType sourceStatType = tm.getSourceStat();
                    StatType targetStatType = tm.getTargetStat();
                    double sourceValue = unit.getStats().get(sourceStatType).getCurrent();
                    double transferringPercent = tm.getTransferPercent();
                    double transferringRatio = tm.getTransferRatio();
                    double toTransfer;
                    double toAdd;

                    if (tm.getTransferType() == TransferType.CONVERSION) {
                        toTransfer = sourceValue * transferringPercent;
                        toTransfer *= amp;
                        toTransfer *= ia;
                        toTransfer *= ua;
                        actualConvert.merge(sourceStatType, toTransfer, Double::sum);
                        toAdd = toTransfer * transferringRatio;
                        added.merge(targetStatType, toAdd, Double::sum);
                    }

                    if (tm.getTransferType() == TransferType.GAIN) {
                        toTransfer = sourceValue * transferringPercent;
                        toTransfer *= amp;
                        toTransfer *= ia;
                        toTransfer *= ua;
                        toAdd = toTransfer * transferringRatio;
                        added.merge(targetStatType, toAdd, Double::sum);
                    }
                }
            }
        }

        for (Map.Entry<StatType, Double> entry : actualConvert.entrySet()) {
            StatType type = entry.getKey();
            double toMinus = (-1) * entry.getValue();
            unit.getStats().get(type).sumToFinal(toMinus);
        }
        for (Map.Entry<StatType, Double> entry : added.entrySet()) {
            StatType type = entry.getKey();
            double toAdd = entry.getValue();
            unit.getStats().get(type).sumToFinal(toAdd);
        }
    }

    public void applyBasicStatWeaponPassive() {
        Set<WeaponType> appliedTypes = new HashSet<>();

        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue; // กัน NPE
            WeaponType weaponType = equipment.getWeaponType();

            // ถ้ายังไม่เคย apply weaponType นี้
            if (!appliedTypes.contains(weaponType)) {
                switch (weaponType) {
                    case CROSSBOW:
                        unit.getStats().get(StatType.CRITDAMAGE).multToCurrent(2);
                        break;
                    case GREATSWORD:
                        unit.getStats().get(StatType.PHYSICALATTACK).multToCurrent(1.3);
                        break;
                    case SNIPER_RIFLE:
                        unit.getStats().get(StatType.PHYSICALPENETRATE).multToCurrent(1.7);
                        break;
                    default:
                        break;
                }
                appliedTypes.add(weaponType); // mark ว่าใช้แล้ว
            }
        }
    }

    public void applyOverrideWeaponPassive() {
        Set<WeaponType> appliedTypes = new HashSet<>();

        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue; // กัน NPE
            WeaponType weaponType = equipment.getWeaponType();

            // ถ้ายังไม่เคย apply weaponType นี้
            if (!appliedTypes.contains(weaponType)) {
                if (weaponType == WeaponType.MIRROR) {
                    unit.getStats().get(StatType.PHYSICALDEFENSE).setCurrent(0);
                    unit.getStats().get(StatType.PHYSICALDEFENSE).setFinal(0);
                }
                appliedTypes.add(weaponType); // mark ว่าใช้แล้ว
            }
        }
    }

    public void resetFinal() {
        for (StatType type : StatType.values()) {
            double current = unit.getStats().get(type).getCurrent();
            unit.getStats().get(type).setFinal(current);
        }
    }

    public void applyOverrideStatModifier() {
        Map<StatType, Double> toOverride = new HashMap<>();
        for (StatType type : StatType.values()) {

            //equipment
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                Equipment equipment = slot.getEquipment();
                if (equipment == null) continue;
                if (equipment.getStatModifiers().get(type) == null) continue;
                if (Double.isNaN(equipment.getStatModifiers().get(type).getOverride())) continue;
                toOverride.put(type, equipment.getStatModifiers().get(type).getOverride());
            }
            //card
            for (CardType cardType : CardType.values()) {
                if (unit.getCard().get(cardType) == null) continue;
                if (unit.getCard().get(cardType).getStatModifiers(cardType).get(type) == null) continue;
                if (Double.isNaN(unit.getCard().get(cardType).getStatModifiers(cardType).get(type).getOverride())) continue;
                toOverride.put(type,unit.getCard().get(cardType).getStatModifiers(cardType).get(type).getOverride());
            }
            //passive
            for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                PassiveNode node = entry.getValue();
                BasicModifier modifier = node.getStatModifiers().get(type);
                if (modifier == null || Double.isNaN(modifier.getOverride())) continue;
                toOverride.put(type, node.getStatModifiers().get(type).getOverride());
            }
            //skills
            for (SkillInstance skillInstance : unit.getAllSkill().values()) {
                BasicModifier modifier = skillInstance.getInstanceBundle().getStatModifiers().get(type);
                if (modifier == null) continue;
                if (!Double.isNaN(modifier.getOverride())) {
                    toOverride.put(type, modifier.getOverride());
                }
            }
            //race
            for (UniqueModifier modifier : unit.getUniqueModifier()) {
                if (modifier.getModifiers().getStatModifiers().get(type) != null) {
                    if (!Double.isNaN(modifier.getModifiers().getStatModifiers().get(type).getOverride())) {
                        toOverride.put(type, modifier.getModifiers().getStatModifiers().get(type).getOverride());
                    }
                }
            }
            //condition
            for (ConditionInstance instance : unit.getConditionInstances().values()) {
                Conditions condition = instance.getCondition();
                if (condition == null) continue;
                if (Double.isNaN(condition.getModifiers().getStatModifierSafe(type).getOverride())) continue;
                toOverride.put(type, condition.getModifiers().getStatModifierSafe(type).getOverride());
            }
        }
        for (Map.Entry<StatType, Double> entry : toOverride.entrySet()) {
            unit.getStats().get(entry.getKey()).setFinal(entry.getValue());
        }
    }

    public void calculateConversion(Map<Integer, TransferModifier> transferModifier,
                                    Map<StatType, Double> stackedConvertPercent,
                                    Map<StatType, Double> actualConvert,
                                    Map<StatType, Double> added) {
        calculateConversion(transferModifier, stackedConvertPercent, actualConvert, added, 1);
    }

    public void calculateConversion(Map<Integer, TransferModifier> transferModifier,
                                    Map<StatType, Double> stackedConvertPercent,
                                    Map<StatType, Double> actualConvert,
                                    Map<StatType, Double> added,
                                    double handMultiplier) {
        for (TransferModifier tm : transferModifier.values()) {
            if (tm.getSourceStat() == null || tm.getTargetStat() == null) continue;

            StatType sourceStatType = tm.getSourceStat();
            StatType targetStatType = tm.getTargetStat();
            double sourceValue = unit.getStats().get(sourceStatType).getCurrent();
            double transferringPercent = tm.getTransferPercent();
            transferringPercent *= handMultiplier;
            double transferringRatio = tm.getTransferRatio();
            double toTransfer;
            double toAdd;

            double equipmentMod = 1;
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                Equipment equipment = slot.getEquipment();
                if (equipment == null) continue;

                double hand_multiplier = 1;
                if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                    hand_multiplier = unit.getMixTwoHandedMult();
                }

                for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                    Double mult = null;
                    Map<EquipmentType, Double> multMap = entry.getValue().getEquipmentSlotMult();
                    if (multMap != null) {
                        mult = multMap.get(equipment.getEquipmentType());
                    }
                    if (mult != null) {
                        equipmentMod += mult * hand_multiplier;
                    }
                }
            }
            transferringRatio *= equipmentMod;

            if (tm.getTransferType() == TransferType.CONVERSION) {
                if (stackedConvertPercent.get(sourceStatType) < 1) {
                    double percentAllocated = stackedConvertPercent.get(sourceStatType);
                    double usableConvertPercent;
                    if (percentAllocated + transferringPercent > 1) {
                        usableConvertPercent = 1 - percentAllocated;
                    } else {
                        usableConvertPercent = transferringPercent;
                    }
                    stackedConvertPercent.merge(sourceStatType, usableConvertPercent, Double::sum);
                    toTransfer = sourceValue * usableConvertPercent;
                    actualConvert.merge(sourceStatType, toTransfer, Double::sum);
                    toAdd = toTransfer * transferringRatio;
                    added.merge(targetStatType, toAdd, Double::sum);
                }
            }
        }
    }

    public void calculateGain(Map<Integer, TransferModifier> transferModifier,
                              Map<StatType, Double> added) {
        calculateGain(transferModifier, added, 1);
    }

    public void calculateGain(Map<Integer, TransferModifier> transferModifier,
                              Map<StatType, Double> added,
                              double handMultiplier) {
        for (TransferModifier tm : transferModifier.values()) {
            if (tm.getSourceStat() == null || tm.getTargetStat() == null) continue;
            StatType sourceStatType = tm.getSourceStat();
            StatType targetStatType = tm.getTargetStat();
            double sourceValue = unit.getStats().get(sourceStatType).getCurrent();
            double transferringPercent = tm.getTransferPercent();
            transferringPercent *= handMultiplier;
            double transferringRatio = tm.getTransferRatio();
            double toTransfer;
            double toAdd;

            double equipmentMod = 1;
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                Equipment equipment = slot.getEquipment();
                if (equipment == null) continue;

                double hand_multiplier = 1;
                if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                    hand_multiplier = unit.getMixTwoHandedMult();
                }

                for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                    Double mult = null;
                    Map<EquipmentType, Double> multMap = entry.getValue().getEquipmentSlotMult();
                    if (multMap != null) {
                        mult = multMap.get(equipment.getEquipmentType());
                    }
                    if (mult != null) {
                        equipmentMod += mult * hand_multiplier;
                    }
                }
            }
            transferringRatio *= equipmentMod;

            if (tm.getTransferType() == TransferType.GAIN) {
                toTransfer = sourceValue * transferringPercent;
                toAdd = toTransfer * transferringRatio;
                added.merge(targetStatType, toAdd, Double::sum);
            }
        }
    }
}