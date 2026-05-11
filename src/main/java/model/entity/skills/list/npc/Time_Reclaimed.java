package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

public class Time_Reclaimed extends Skill {

    public static String NAME = "Time, Reclaimed";

    public Time_Reclaimed() {
        super();
        setDescription("เริ่มต้นเทิร์นของยูนิตพันธมิตรทั้งหมดที่ผ่านมาในรอบเทิร์นนี้อีกครั้ง ยูนิตที่เทิร์นถูกเริ่มต้นใหม่ด้วยสกิลนี้จะใช้งาน Action ไม่ได้");
        setActionType("Turn");
        setManaCost(16);
        setCooldown(6);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.TIME);
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
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
