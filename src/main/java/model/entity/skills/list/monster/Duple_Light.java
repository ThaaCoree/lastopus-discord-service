package model.entity.skills.list.monster;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import util.LogWriterUtil;

public class Duple_Light extends Skill {

    public static String NAME = "Duple Light";

    public Duple_Light() {
        super();
        setDescription("เมื่อสร้างความเสียหายสำเร็จ จู่โจมเพิ่มอีกสองครั้ง ครั้งแรกสร้างความเสียหายกายภาพธาตุแสง XA หน่วย ครั้งที่สองสร้างความเสียหายกายภาพธาตุมืด XB หน่วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*RATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);

        getSkillMultiplier().put("XB",new SkillMultiplier("1*RATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DARK);
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
        LogWriterUtil.log("Duple Light deals "+(xa)+" physical damage each time", combatFlow.getTurnCount());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
