package calculator;

import model.entity.*;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.skills.Skill;
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

public class StatusCalculator {

    private final Unit unit;

    public StatusCalculator(Unit unit) {
        this.unit = unit;
    }

    public void calculateBaseStatusFromRaisedStatuses() {
        // รีเซ็ตค่า status ทั้งหมดให้เป็นค่าเริ่มต้นก่อน
        for (StatusType type : unit.getStatuses().keySet()) {
            unit.getStatuses().put(type, new ModValue(1.0));
        }
        //apply ค่า raisedStatus
        for (StatusType type : unit.getStatuses().keySet()) {
            double raisedStatus = unit.getRaisedStatuses().getOrDefault(type, 0) + unit.getStatuses().get(type).getBase();
            unit.getStatuses().get(type).setBase(raisedStatus);
        }
        //ในเบื้องต้น ทำให้ Current กับ Final มีค่าเท่ากับ Base ก่อน
        for (StatusType type : unit.getStatuses().keySet()) {
            double baseStatus = unit.getStatuses().get(type).getBase();
            unit.getStatuses().get(type).setCurrent(baseStatus);
            unit.getStatuses().get(type).setFinal(baseStatus);
        }
    }

    public void applyBasicStatusModifiers() {
        Map<StatusType, Double> equipFlatSum = new HashMap<>();
        Map<StatusType, Double> equipMultSum = new HashMap<>();
        Map<StatusType, Double> passiveFlatSum = new HashMap<>();
        Map<StatusType, Double> passiveMultSum = new HashMap<>();
        Map<StatusType, Double> cardFlatSum = new HashMap<>();
        Map<StatusType, Double> raceFlatSum = new HashMap<>();
        Map<StatusType, Double> skillFlatSum = new HashMap<>();
        Map<StatusType, Double> globalMultProduct = new HashMap<>();

        // เริ่มต้นค่า
        for (StatusType type : StatusType.values()) {
            equipFlatSum.put(type, 0.0);
            equipMultSum.put(type, 0.0);
            passiveFlatSum.put(type, 0.0);
            passiveMultSum.put(type, 0.0);
            globalMultProduct.put(type, 1.0);
            raceFlatSum.put(type, 0.0);
            skillFlatSum.put(type, 0.0);
            cardFlatSum.put(type, 0.0);
        }

        for (StatusType statusType : StatusType.values()) {
            for (UniqueModifier modifier : unit.getUniqueModifier()) {
                if (modifier.getModifiers().getStatusModifiers().get(statusType) != null) {
                    double toAdd = modifier.getModifiers().getStatusModifiers().get(statusType).getFlat();
                    raceFlatSum.merge(statusType, toAdd, Double::sum);
                }
            }

            for (CardType cardType : CardType.values()) {
                if (unit.getCard().get(cardType) == null) continue;
                if (unit.getCard().get(cardType).getStatusModifiers(cardType).get(statusType) != null) {
                    double cardToAdd = unit.getCard().get(cardType).getStatusModifiers(cardType).get(statusType).getFlat();
                    cardFlatSum.merge(statusType, cardToAdd, Double::sum);
                }
            }
        }

        //skills
        for (SkillInstance instance : unit.getAllSkill().values()) {
            for (StatusType type : StatusType.values()) {
                if (instance.getInstanceBundle().getStatusModifiers().get(type) == null) continue;
                double skillToAdd = instance.getInstanceBundle().getStatusModifiers().get(type).getFlat();
                skillFlatSum.merge(type, skillToAdd, Double::sum);
            }
        }

        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue;

            double handMultiplier = 1;
            if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                handMultiplier = unit.getMixTwoHandedMult();
            }

            for (StatusType type : StatusType.values()) {
                BasicModifier modifier = equipment.getStatusModifiers().get(type);
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
                passiveMultSum.merge(type,modifier.getPassiveMult() * handMultiplier * equipmentMod, Double::sum);
            }
        }
        
        for (PassiveNode node : unit.getAllocatedPassives().values()) {
            if (node == null) continue;

            for (StatusType type : StatusType.values()) {
                BasicModifier modifier = node.getStatusModifiers().get(type);
                if (modifier == null) continue;

                passiveFlatSum.merge(type, modifier.getFlat(), Double::sum);
                equipMultSum.merge(type, modifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type,modifier.getPassiveMult(), Double::sum);
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
            for (StatusType type : StatusType.values()) {
                BasicModifier modifier = condition.getModifiers().getStatusModifiers().get(type);
                if (modifier == null) continue;
                equipMultSum.merge(type, modifier.getEquipmentMult() * amp * ia * ua * resist, Double::sum);
                passiveMultSum.merge(type, modifier.getPassiveMult() * amp * ia * ua * resist, Double::sum);
            }
        }

        for (StatusType type : StatusType.values()) {
            double modifier = 0;
            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                if (node.getStatusModifiers().get(type) == null) continue;
                modifier = node.getStatusModifiers().get(type).getGlobalMult();
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
                if (slot.getEquipment().getStatusModifiers().get(type) == null) continue;
                modifier = slot.getEquipment().getStatusModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type, ((modifier * handMultiplier * equipmentMod * (1+equipMultSum.get(type))+1)), (oldVal, newVal) -> oldVal * newVal);
            }
            for (SkillInstance instance : unit.getAllSkill().values()) {
                if (instance.getSkillData() == null) continue;
                if (instance.getSkillData().getSkillModifier().getStatusModifiers().get(type) == null) continue;
                modifier = instance.getSkillData().getSkillModifier().getStatusModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                double skillToMult = instance.getInstanceBundle().getStatusModifierSafe(type).getGlobalMult();
                globalMultProduct.merge(type, skillToMult+1, (oldVal, newVal) -> oldVal * newVal);
            }
            for (CardType cardType : unit.getCard().keySet()) {
                if (unit.getCard().get(cardType).getStatusModifiers(cardType).get(type) == null) continue;
                modifier = unit.getCard().get(cardType).getStatusModifiers(cardType).get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
            for (UniqueModifier uniqueModifier : unit.getUniqueModifier()) {
                if (uniqueModifier.getModifiers().getStatusModifiers().get(type) == null) continue;
                modifier = uniqueModifier.getModifiers().getStatusModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
        }

        // Apply Flat + EquipMult + PassiveMult
        for (StatusType type : StatusType.values()) {
            double equipFlat = equipFlatSum.get(type);
            double equipMult = equipMultSum.get(type);
            double passiveFlat = passiveFlatSum.get(type);
            double passiveMult = passiveMultSum.get(type);
            double raceFlat = raceFlatSum.get(type);
            double cardFlat = cardFlatSum.get(type);
            double skillFlat = skillFlatSum.get(type);
            unit.getStatuses().get(type).sumToCurrent(equipFlat + passiveFlat);
            unit.getStatuses().get(type).sumToCurrent(equipFlat * equipMult);
            unit.getStatuses().get(type).sumToCurrent(passiveFlat * passiveMult);
            unit.getStatuses().get(type).sumToCurrent(raceFlat);
            unit.getStatuses().get(type).sumToCurrent(cardFlat);
            unit.getStatuses().get(type).sumToCurrent(skillFlat);
        }

        // Apply Global Mult
        for (StatusType type : StatusType.values()) {
            unit.getStatuses().get(type).multToCurrent(globalMultProduct.getOrDefault(type, 1.0));
        }
    }

    public void applyTransferStatusModifier() {
        Map<StatusType, Double> stackedConvertPercent = new HashMap<>();
        for (StatusType type : StatusType.values()) {
            stackedConvertPercent.put(type, 0.0);
        }
        Map<StatusType, Double> actualConvert = new HashMap<>();
        Map<StatusType, Double> added = new HashMap<>();
        Map<StatusType, Double> globalMultProduct = new HashMap<>();
        Map<StatusType, Double> passiveMultSum = new HashMap<>();
        Map<StatusType, Double> equipMultSum = new HashMap<>();

        for (StatusType type : StatusType.values()) {
            globalMultProduct.put(type, 1.0);
            double modifier = 0;

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                BasicModifier basicModifier = node.getStatusModifiers().get(type);
                if (basicModifier == null) continue;
                equipMultSum.merge(type, basicModifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, basicModifier.getPassiveMult(), Double::sum);
            }
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                if (slot.getEquipment() == null) continue;
                BasicModifier basicModifier = slot.getEquipment().getStatusModifiers().get(type);
                if (basicModifier == null) continue;
                equipMultSum.merge(type, basicModifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, basicModifier.getPassiveMult(), Double::sum);
            }

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                if (node.getStatusModifiers().get(type) == null) continue;
                modifier = node.getStatusModifiers().get(type).getGlobalMult();
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
                if (slot.getEquipment().getStatusModifiers().get(type) == null) continue;
                modifier = slot.getEquipment().getStatusModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type, ((modifier * handMultiplier * equipmentMod*(1+equipMultSum.get(type)))+1), (oldVal, newVal) -> oldVal * newVal);
            }
            for (SkillInstance instance : unit.getAllSkill().values()) {
                if (instance.getSkillData() == null) continue;
                if (instance.getSkillData().getSkillModifier().getStatusModifiers().get(type) == null) continue;
                modifier = instance.getSkillData().getSkillModifier().getStatusModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                double skillToMult = instance.getInstanceBundle().getStatusModifiers().get(type).getGlobalMult();
                globalMultProduct.merge(type, skillToMult+1, (oldVal, newVal) -> oldVal * newVal);
            }
            for (CardType cardType : unit.getCard().keySet()) {
                if (unit.getCard().get(cardType).getStatusModifiers(cardType).get(type) == null) continue;
                modifier = unit.getCard().get(cardType).getStatusModifiers(cardType).get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
            for (UniqueModifier uniqueModifier : unit.getUniqueModifier()) {
                if (uniqueModifier.getModifiers().getStatusModifiers().get(type) == null) continue;
                modifier = uniqueModifier.getModifiers().getStatusModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(type,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
        }

        //convert unique
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getModifiers().getTransferModifiers() != null) {
                calculateConversion(modifier.getModifiers().getTransferModifiers(), stackedConvertPercent, actualConvert, added);
            }
        }

        for (SkillInstance instance : unit.getAllSkill().values()) {
            if (instance.getInstanceBundle().getTransferModifiers() == null) continue;
            calculateConversion(instance.getInstanceBundle().getTransferModifiers(), stackedConvertPercent, actualConvert, added);
        }

        //convert passive
        for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
            PassiveNode node = entry.getValue();

            if (node.getTransferModifiers() != null) {
                calculateConversion(node.getTransferModifiers(), stackedConvertPercent,actualConvert,added);
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
                calculateConversion(equipment.getTransferModifiers(),stackedConvertPercent,actualConvert,added, handMultiplier);
            }
        }
        //convert card
        for (Map.Entry<CardType, Card> entry : unit.getCard().entrySet()) {
            CardType type = entry.getKey();
            Card card = entry.getValue();
            if (card.getTransferModifiers(type) != null) {
                calculateConversion(card.getTransferModifiers(type),stackedConvertPercent,actualConvert,added);
            }
        }

        for (Map.Entry<StatusType, Double> entry : actualConvert.entrySet()) {
            StatusType type = entry.getKey();
            double toMinus = (-1) * entry.getValue();
            unit.getStatuses().get(type).sumToCurrent(toMinus);
        }
        for (Map.Entry<StatusType, Double> entry : added.entrySet()) {
            StatusType type = entry.getKey();
            double toAdd = entry.getValue();
            toAdd *= globalMultProduct.get(entry.getKey());
            unit.getStatuses().get(type).sumToCurrent(toAdd);
        }

        added.clear(); //clear ก่อนไป gain ต่อ เพื่อไม่ให้ transfer added มัน stack

        //gain race
        for (UniqueModifier modifier : unit.getUniqueModifier()) {
            if (modifier.getModifiers().getTransferModifiers() != null) {
                calculateGain(modifier.getModifiers().getTransferModifiers(), added);
            }
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
                calculateGain(card.getTransferModifiers(type),added);
            }
        }

        for (Map.Entry<StatusType, Double> entry : added.entrySet()) {
            StatusType type = entry.getKey();
            double toAdd = entry.getValue();
            toAdd *= globalMultProduct.get(entry.getKey());
            unit.getStatuses().get(type).sumToCurrent(toAdd);
        }
    }

    public void applyConditionsStatusModifier() {
        resetFinal();
        if (unit.getConditionInstances().isEmpty()) return;
        Map<StatusType, Double> conditionFlatSum = new HashMap<>();
        Map<StatusType, Double> conditionMultProduct = new HashMap<>();
        for (StatusType type : StatusType.values()) {
            conditionFlatSum.put(type,0.0);
            conditionMultProduct.put(type,1.0);
        }

        for (ConditionInstance instance : unit.getConditionInstances().values()) {
            Conditions condition = instance.getCondition();
            if (condition == null) continue;
            Unit source = instance.getSource();
            double amp = 1;
//            if (source != null) {
//                if (condition.getConditionType() == ConditionType.BUFF) {
//                    amp += source.getStats().get(StatType.BUFFAMPLIFIER).getFinal();
//                }
//                if (condition.getConditionType() == ConditionType.DEBUFF) {
//                    amp += source.getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
//                }
//            }
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
            for (StatusType type : StatusType.values()) {
                BasicModifier modifier = condition.getModifiers().getStatusModifiers().get(type);
                if (modifier == null) continue;
                conditionFlatSum.merge(type, modifier.getFlat() * amp * ia * ua * resist, Double::sum);
                conditionMultProduct.merge(type, (modifier.getGlobalMult() * amp * ia * ua * resist)+1, (oldVal, newVal) -> oldVal * newVal);
            }
        }
        // Apply Flat + Global
        for (StatusType type : StatusType.values()) {
            double conditionFlat = conditionFlatSum.getOrDefault(type, 0.0);
            unit.getStatuses().get(type).sumToFinal(conditionFlat);
            unit.getStatuses().get(type).multToFinal(conditionMultProduct.getOrDefault(type, 1.0));
        }

        Map<StatusType, Double> actualConvert = new HashMap<>();
        Map<StatusType, Double> added = new HashMap<>();

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
                    if (tm.getSourceStatus() == null || tm.getTargetStatus() == null) continue;

                    StatusType sourceStatusType = tm.getSourceStatus();
                    StatusType targetStatusType = tm.getTargetStatus();
                    double sourceValue = unit.getStatuses().get(sourceStatusType).getCurrent();
                    double transferringPercent = tm.getTransferPercent();
                    double transferringRatio = tm.getTransferRatio();
                    double toTransfer;
                    double toAdd;

                    if (tm.getTransferType() == TransferType.CONVERSION) {
                            toTransfer = sourceValue * transferringPercent;
                            toTransfer *= amp;
                            toTransfer *= ia;
                            toTransfer *= ua;
                            actualConvert.merge(sourceStatusType, toTransfer, Double::sum);
                            toAdd = toTransfer * transferringRatio;
                            added.merge(targetStatusType, toAdd, Double::sum);
                    }

                    if (tm.getTransferType() == TransferType.GAIN) {
                        toTransfer = sourceValue * transferringPercent;
                        toTransfer *= amp;
                        toTransfer *= ia;
                        toTransfer *= ua;
                        toAdd = toTransfer * transferringRatio;
                        added.merge(targetStatusType, toAdd, Double::sum);
                    }
                }
            }
        }

        for (Map.Entry<StatusType, Double> entry : actualConvert.entrySet()) {
            StatusType type = entry.getKey();
            double toMinus = (-1) * entry.getValue();
            unit.getStatuses().get(type).sumToFinal(toMinus);
        }
        for (Map.Entry<StatusType, Double> entry : added.entrySet()) {
            StatusType type = entry.getKey();
            double toAdd = entry.getValue();
            unit.getStatuses().get(type).sumToFinal(toAdd);
        }
    }

    public void limitHumanWeakStatus() {
        if (unit.getRace().getName() != null) {
            if (!unit.getRace().getName().equals("Human")) return;
            StatusType limitedStatus = unit.getRace().getWeakStatus();
            double currentStatus = unit.getStatuses().get(limitedStatus).getCurrent();
            double level = unit.getLevel();
            if (currentStatus > level) {
                unit.getStatuses().get(limitedStatus).setCurrent(level);
            }
        }
    }

    public void applyBasicStatusWeaponPassive() {
        Set<WeaponType> appliedTypes = new HashSet<>();

        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue; // กัน NPE
            WeaponType weaponType = equipment.getWeaponType();

            if (!appliedTypes.contains(weaponType)) {
                switch (weaponType) {
                    case STAFF:
                        unit.getStatuses().get(StatusType.INTELLIGENCE).multToCurrent(1.3);
                        break;
                    default:
                        break;
                }

                appliedTypes.add(weaponType); // mark ว่าใช้แล้ว
            }
        }
    }

    public void resetFinal() {
        for (StatusType type : StatusType.values()) {
            double current = unit.getStatuses().get(type).getCurrent();
            unit.getStatuses().get(type).setFinal(current);
        }
    }

    public void applyOverrideStatusModifier() {
        Map<StatusType, Double> toOverride = new HashMap<>();
        for (StatusType type : StatusType.values()) {

            //equipment
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                Equipment equipment = slot.getEquipment();
                if (equipment == null) continue;
                if (equipment.getStatusModifiers().get(type) == null) continue;
                if (Double.isNaN(equipment.getStatusModifiers().get(type).getOverride())) continue;
                toOverride.put(type, equipment.getStatusModifiers().get(type).getOverride());
            }
            //card
            for (CardType cardType : CardType.values()) {
                if (unit.getCard().get(cardType) == null) continue;
                if (unit.getCard().get(cardType).getStatusModifiers(cardType).get(type) == null) continue;
                if (Double.isNaN(unit.getCard().get(cardType).getStatusModifiers(cardType).get(type).getOverride())) continue;
                toOverride.put(type,unit.getCard().get(cardType).getStatusModifiers(cardType).get(type).getOverride());
            }
            //passive
            for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                PassiveNode node = entry.getValue();
                BasicModifier modifier = node.getStatusModifiers().get(type);
                if (modifier == null || Double.isNaN(modifier.getOverride())) continue;
                toOverride.put(type, node.getStatusModifiers().get(type).getOverride());
            }
            //skills
            for (SkillInstance skillInstance : unit.getAllSkill().values()) {
                BasicModifier modifier = skillInstance.getInstanceBundle().getStatusModifiers().get(type);
                if (modifier == null) continue;
                if (!Double.isNaN(modifier.getOverride())) {
                    toOverride.put(type, modifier.getOverride());
                }
            }
            //unique
            for (UniqueModifier modifier : unit.getUniqueModifier()) {
                if (modifier.getModifiers().getStatusModifiers().get(type) != null) {
                    if (!Double.isNaN(modifier.getModifiers().getStatusModifiers().get(type).getOverride())) {
                        toOverride.put(type, modifier.getModifiers().getStatusModifiers().get(type).getOverride());
                    }
                }
            }
            //condition
            for (ConditionInstance instance : unit.getConditionInstances().values()) {
                Conditions condition = instance.getCondition();
                if (condition == null) continue;
                if (condition.getModifiers() == null) continue;
                if (Double.isNaN(condition.getModifiers().getStatusModifierSafe(type).getOverride())) continue;
                toOverride.put(type, condition.getModifiers().getStatusModifierSafe(type).getOverride());
            }
        }
        for (Map.Entry<StatusType, Double> entry : toOverride.entrySet()) {
            unit.getStatuses().get(entry.getKey()).setFinal(entry.getValue());
        }
    }

    public void calculateEquality() {
        for (PassiveNode node : unit.getAllocatedPassives().values()) {
            if (node.getName().equals("Equality")) {
                double str = unit.getStatuses().get(StatusType.STRENGTH).getCurrent();
                double agi = unit.getStatuses().get(StatusType.AGILITY).getCurrent();
                double vit = unit.getStatuses().get(StatusType.VITALITY).getCurrent();
                double dex = unit.getStatuses().get(StatusType.DEXTERITY).getCurrent();
                double wis = unit.getStatuses().get(StatusType.WISDOM).getCurrent();
                double intel = unit.getStatuses().get(StatusType.INTELLIGENCE).getCurrent();
                double luk = unit.getStatuses().get(StatusType.LUCK).getCurrent();

                double sum = str+agi+vit+dex+wis+intel+luk;
                unit.getStatuses().get(StatusType.STRENGTH).setCurrent(sum/7);
                unit.getStatuses().get(StatusType.AGILITY).setCurrent(sum/7);
                unit.getStatuses().get(StatusType.VITALITY).setCurrent(sum/7);
                unit.getStatuses().get(StatusType.DEXTERITY).setCurrent(sum/7);
                unit.getStatuses().get(StatusType.WISDOM).setCurrent(sum/7);
                unit.getStatuses().get(StatusType.INTELLIGENCE).setCurrent(sum/7);
                unit.getStatuses().get(StatusType.LUCK).setCurrent(sum/7);
            }
        }
    }

    public void calculateConversion(Map<Integer, TransferModifier> transferModifier,
                                    Map<StatusType, Double> stackedConvertPercent,
                                    Map<StatusType, Double> actualConvert,
                                    Map<StatusType, Double> added){
        calculateConversion(transferModifier, stackedConvertPercent, actualConvert, added, 1);
    }

    public void calculateConversion(Map<Integer, TransferModifier> transferModifier,
                                    Map<StatusType, Double> stackedConvertPercent,
                                    Map<StatusType, Double> actualConvert,
                                    Map<StatusType, Double> added,
                                    double handMultiplier){
        for (TransferModifier tm : transferModifier.values()) {
            if (tm.getSourceStatus() == null || tm.getTargetStatus() == null) continue;

            StatusType sourceStatusType = tm.getSourceStatus();
            StatusType targetStatusType = tm.getTargetStatus();
            double sourceValue = unit.getStatuses().get(sourceStatusType).getCurrent();
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
                if (stackedConvertPercent.get(sourceStatusType) < 1) {
                    double percentAllocated = stackedConvertPercent.get(sourceStatusType);
                    double usableConvertPercent;
                    if (percentAllocated + transferringPercent > 1) {
                        usableConvertPercent = 1 - percentAllocated;
                    } else {
                        usableConvertPercent = transferringPercent;
                    }
                    stackedConvertPercent.merge(sourceStatusType, usableConvertPercent, Double::sum);
                    toTransfer = sourceValue * usableConvertPercent;
                    actualConvert.merge(sourceStatusType, toTransfer, Double::sum);
                    toAdd = toTransfer * transferringRatio;
                    added.merge(targetStatusType, toAdd, Double::sum);
                }
            }
        }
    }

    public void calculateGain(Map<Integer, TransferModifier> transferModifier,
                              Map<StatusType, Double> added) {
        calculateGain(transferModifier, added, 1);
    }
    public void calculateGain(Map<Integer, TransferModifier> transferModifier,
                              Map<StatusType, Double> added,
                              double handMultiplier) {
        for (TransferModifier tm : transferModifier.values()) {
            if (tm.getSourceStatus() == null || tm.getTargetStatus() == null) continue;
            StatusType sourceStatusType = tm.getSourceStatus();
            StatusType targetStatusType = tm.getTargetStatus();
            double sourceValue = unit.getStatuses().get(sourceStatusType).getCurrent();
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
                added.merge(targetStatusType, toAdd, Double::sum);
            }
        }
    }
}
