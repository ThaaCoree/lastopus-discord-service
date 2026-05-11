package model.entity.skills.list.item.support;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import util.LogWriterUtil;

public class Grow extends Skill {

    public static String NAME = "Grow";

    public Grow() {
        super();
        setDescription("ใช้งานได้เมื่อมี MP เหลืออย่างน้อย XA หน่วยเท่านั้น\n" +
                "เมื่อใช้งาน มอบหนึ่ง Action ให้กับพันธมิตรทั้งหมดในสนาม จากนั้นทำให้ MP ของผู้ใช้เหลือ 0");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
        getSkillMultiplier().put("XA",new SkillMultiplier("20"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);
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
        if (getUser().getMana().getRemaining() < 50) return;
        LogWriterUtil.log("Grow activated, giving 1 action to all allies", combatFlow.getTurnCount());
        getUser().setRemainingMana(0);
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
