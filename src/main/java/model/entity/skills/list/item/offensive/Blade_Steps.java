package main.java.model.entity.skills.list.item.offensive;

import main.java.controller.CombatFlow;
import main.java.model.entity.Card;
import main.java.model.entity.PassiveNode;
import main.java.model.entity.UniqueModifier;
import main.java.model.entity.items.Equipment;
import main.java.model.entity.items.EquipmentSlot;
import main.java.model.entity.skills.*;
import main.java.model.modifier.TransferModifier;
import main.java.model.type.*;

import java.util.HashMap;
import java.util.Map;

public class Blade_Steps extends Skill {

    public static String NAME = "Blade Steps";

    public Blade_Steps() {
        super();
        setDescription("สร้างความเสียหายกายภาพระยะประชิดพื้นฐานเพิ่มขึ้น XA หน่วยทุกเมตรที่เคลื่อนไหวก่อนการจู่โจม\n" +
                "รับความเสียหายย้อนกลับจากการโจมตีผนวกความเร็วน้อยลงเล็กน้อย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
//                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
//        spec    .addFields(
//                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
//                        .options(List.of("choice","choice"), 0)
//                        .labelProvider(String::toString, 0)
//        , 0, 0)
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {

        Map<StatType, Double> stackedConvertPercent = new HashMap<>();
        for (StatType type : StatType.values()) {
            stackedConvertPercent.put(type, 0.0);
        }
        Map<StatType, Double> actualConvert = new HashMap<>();
        Map<StatType, Double> added = new HashMap<>();

        //convert unique
        for (UniqueModifier modifier : getUser().getUniqueModifier()) {
            if (modifier.getModifiers().getTransferModifiers() != null) {
                calculateConversion(modifier.getModifiers().getTransferModifiers(), stackedConvertPercent, actualConvert, added);
            }
        }

        //convert skills
        for (SkillInstance instance : getUser().getAllSkill().values()) {
            if (instance.getInstanceBundle().getTransferModifiers() == null) continue;
            calculateConversion(instance.getInstanceBundle().getTransferModifiers(), stackedConvertPercent, actualConvert, added);
        }

        //convert passive
        for (Map.Entry<Integer, PassiveNode> entry : getUser().getAllocatedPassives().entrySet()) {
            PassiveNode node = entry.getValue();

            if (node.getTransferModifiers() != null) {
                calculateConversion(node.getTransferModifiers(), stackedConvertPercent, actualConvert, added);
            }
        }
        //convert equipment
        for (EquipmentSlot slot : getUser().getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue;

            double handMultiplier = 1;
            if (getUser().isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                handMultiplier = getUser().getMixTwoHandedMult();
            }
            if (equipment.getTransferModifiers() != null) {
                calculateConversion(equipment.getTransferModifiers(), stackedConvertPercent, actualConvert, added, handMultiplier);
            }
        }
        //convert card
        for (Map.Entry<CardType, Card> entry : getUser().getCard().entrySet()) {
            CardType type = entry.getKey();
            Card card = entry.getValue();
            if (card.getTransferModifiers(type) != null) {
                calculateConversion(card.getTransferModifiers(type), stackedConvertPercent, actualConvert, added);
            }
        }

        for (Map.Entry<StatType, Double> entry : added.entrySet()) {
            StatType type = entry.getKey();
            double toRemove = (entry.getValue() * 0.9) * -1;
            getSkillModifier().getStatModifierSafe(type).setFlat(toRemove);
        }
        
        added.clear();

        //gain unique
        for (UniqueModifier modifier : getUser().getUniqueModifier()) {
            if (modifier.getModifiers().getTransferModifiers() != null) {
                calculateGain(modifier.getModifiers().getTransferModifiers(), added);
            }
        }

        //gain skills
        for (SkillInstance instance : getUser().getAllSkill().values()) {
            if (instance.getInstanceBundle().getTransferModifiers() == null) continue;
            calculateGain(instance.getInstanceBundle().getTransferModifiers(), added);
        }

        //gain passive
        for (Map.Entry<Integer, PassiveNode> entry : getUser().getAllocatedPassives().entrySet()) {
            PassiveNode node = entry.getValue();
            if (node.getTransferModifiers() != null) {
                calculateGain(node.getTransferModifiers(), added);
            }
        }

        //gain equipment
        for (EquipmentSlot slot : getUser().getEquipmentSlots().values()) {
            Equipment equipment = slot.getEquipment();
            if (equipment == null) continue;

            double handMultiplier = 1;
            if (getUser().isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                handMultiplier = getUser().getMixTwoHandedMult();
            }
            if (equipment.getTransferModifiers() != null) {
                calculateGain(equipment.getTransferModifiers(), added, handMultiplier);
            }
        }

        //gain card
        for (Map.Entry<CardType, Card> entry : getUser().getCard().entrySet()) {
            CardType type = entry.getKey();
            Card card = entry.getValue();
            if (card.getTransferModifiers(type) != null) {
                calculateGain(card.getTransferModifiers(type), added);
            }
        }

        for (Map.Entry<StatType, Double> entry : added.entrySet()) {
            StatType type = entry.getKey();
            double toRemove = (entry.getValue() * 0.9) * -1;
            getSkillModifier().getStatModifierSafe(type).setFlat(toRemove);
        }

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

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
            if (tm.getSourceStat() != StatType.MOVEMENTSPEED) continue;
            StatType sourceStatType = tm.getSourceStat();
            StatType targetStatType = tm.getTargetStat();
            double sourceValue = getUser().getStats().get(sourceStatType).getCurrent();
            double transferringPercent = tm.getTransferPercent();
            transferringPercent *= handMultiplier;
            double transferringRatio = tm.getTransferRatio();
            double toTransfer;
            double toAdd;
            if (tm.getTransferType() == TransferType.GAIN) {
                toTransfer = sourceValue * transferringPercent;
                toAdd = toTransfer * transferringRatio;
                added.merge(targetStatType, toAdd, Double::sum);
            }
        }
    }

    public void calculateConversion(Map<Integer, TransferModifier> transferModifier,
                                    Map<StatType, Double> stackedConvertPercent,
                                    Map<StatType, Double> actualConvert,
                                    Map<StatType, Double> added,
                                    double handMultiplier) {
        for (TransferModifier tm : transferModifier.values()) {
            if (tm.getSourceStat() == null || tm.getTargetStat() == null) continue;
            if (tm.getSourceStat() != StatType.MOVEMENTSPEED) continue;

            StatType sourceStatType = tm.getSourceStat();
            StatType targetStatType = tm.getTargetStat();
            double sourceValue = getUser().getStats().get(sourceStatType).getCurrent();
            double transferringPercent = tm.getTransferPercent();
            transferringPercent *= handMultiplier;
            double transferringRatio = tm.getTransferRatio();
            double toTransfer;
            double toAdd;

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

    public void calculateConversion(Map<Integer, TransferModifier> transferModifier,
                                    Map<StatType, Double> stackedConvertPercent,
                                    Map<StatType, Double> actualConvert,
                                    Map<StatType, Double> added) {
        calculateConversion(transferModifier, stackedConvertPercent, actualConvert, added, 1);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
