package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.modifier.TransferModifier;
import model.type.SkillType;
import model.type.StatusType;
import model.type.TransferType;

public class Sixth_Sense extends Skill {

    public static String NAME = "Sixth Sense";

    public Sixth_Sense() {
        super();
        setDescription("เพิ่ม AGI และ VIT จาก XA ของ DEX");
        setActionType("Passive");
        setManaReservePercent(0.2);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.25"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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
        TransferModifier transfer = new TransferModifier();
        double xa = getSkillMultiplier().get("XA").getResult();
        transfer.setTransferType(TransferType.GAIN);
        transfer.setSourceStatus(StatusType.DEXTERITY);
        transfer.setTargetStatus(StatusType.AGILITY);
        transfer.setTransferPercent(xa);
        transfer.setTransferRatio(1);
        getSkillModifier().getTransferModifiers().put(1, transfer);

        TransferModifier transfer2 = new TransferModifier();
        transfer2.setTransferType(TransferType.GAIN);
        transfer2.setSourceStatus(StatusType.DEXTERITY);
        transfer2.setTargetStatus(StatusType.VITALITY);
        transfer2.setTransferPercent(xa);
        transfer2.setTransferRatio(1);
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
