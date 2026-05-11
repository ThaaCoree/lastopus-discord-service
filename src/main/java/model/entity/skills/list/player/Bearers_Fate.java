package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.model.entity.items.Equipment;
import main.java.model.entity.items.EquipmentSlot;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.modifier.TransferModifier;
import main.java.model.type.EquipmentType;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;
import main.java.model.type.TransferType;

import java.util.Map;

public class Bearers_Fate extends Skill {

    public static String NAME = "Bearer's Fate";

    public Bearers_Fate() {
        super();
        setDescription("หากกำลังสวมใส่อาวุธที่มอบ ATK ประเภทเดียว, Convert XA ของ ATK อีกสองค่าให้ตรงกับ ATK ที่อาวุธกำลังมอบให้");
        setActionType("Passive");
        setManaReservePercent(0.8);

        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

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
        double xa = getSkillMultiplier().get("XA").getResult();

        int givingPATK = 0;
        int givingMATK = 0;
        int givingRATK = 0;

        for (Map.Entry<Integer, EquipmentSlot> entry : getUser().getEquipmentSlots().entrySet()) {
            if (entry.getValue().getEquipment() == null) break;
            if (entry.getValue().getEquipmentType().equals(EquipmentType.WEAPON)) {
                Equipment equipment = entry.getValue().getEquipment();
                if (equipment.getStatModifiers().get(StatType.PHYSICALATTACK) != null) {
                    givingPATK = 1;
                }
                if (equipment.getStatModifiers().get(StatType.MAGICALATTACK) != null) {
                    givingMATK = 1;
                }
                if (equipment.getStatModifiers().get(StatType.RANGEDATTACK) != null) {
                    givingRATK = 1;
                }
            }
        }
        calculateAllMultiplier();
        TransferModifier transfer1 = new TransferModifier();
        transfer1.setTransferType(TransferType.CONVERSION);
        TransferModifier transfer2 = new TransferModifier();
        transfer2.setTransferType(TransferType.CONVERSION);

        if (givingPATK == 1 && givingMATK == 0 && givingRATK == 0) {
            transfer1.setSourceStat(StatType.RANGEDATTACK);
            transfer1.setTargetStat(StatType.PHYSICALATTACK);
            transfer2.setSourceStat(StatType.MAGICALATTACK);
            transfer2.setTargetStat(StatType.PHYSICALATTACK);
        }
        if (givingPATK == 0 && givingMATK == 1 && givingRATK == 0) {
            transfer1.setSourceStat(StatType.RANGEDATTACK);
            transfer1.setTargetStat(StatType.MAGICALATTACK);
            transfer2.setSourceStat(StatType.PHYSICALATTACK);
            transfer2.setTargetStat(StatType.MAGICALATTACK);
        }
        if (givingPATK == 0 && givingMATK == 0 && givingRATK == 1) {
            transfer1.setSourceStat(StatType.PHYSICALATTACK);
            transfer1.setTargetStat(StatType.RANGEDATTACK);
            transfer2.setSourceStat(StatType.MAGICALATTACK);
            transfer2.setTargetStat(StatType.RANGEDATTACK);
        }
        transfer1.setTransferPercent(xa);
        transfer1.setTransferRatio(1);
        transfer2.setTransferPercent(xa);
        transfer2.setTransferRatio(1);
        getSkillModifier().getTransferModifiers().put(1, transfer1);
        getSkillModifier().getTransferModifiers().put(2, transfer2);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
