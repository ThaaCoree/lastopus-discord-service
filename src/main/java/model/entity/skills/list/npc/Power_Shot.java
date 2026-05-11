package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;
import util.LogWriterUtil;

public class Power_Shot extends Skill {

    public static String NAME = "Power Shot";

    public Power_Shot() {
        super();
        setDescription("นำ Action และ Combined Action ทั้งหมดมาคำนวณเป็นความเสียหายของสกิลนี้แล้วธนูออกไปลูกหนึ่ง\n" +
                "สร้างความเสียหายเพิ่มเติม XA หน่วยต่อ Action และเพิ่มอีก XB หน่วยต่อ Combined Action ที่สูญเสีย");
        setActionType("Turn");
        setManaCost(5);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.8*RATK*AttackSPD"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RESOURCE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SINGLE_TARGET);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.35*RATK*AttackSPD"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.RESOURCE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SINGLE_TARGET);
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        double ratk = getUser().getStats().get(StatType.RANGEDATTACK).getFinal();
        LogWriterUtil.log("Power Shot deals "+(xa+xb+ratk)+" physical damage", combatFlow.getTurnCount());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
