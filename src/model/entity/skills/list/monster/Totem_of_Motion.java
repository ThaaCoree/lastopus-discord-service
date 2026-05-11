package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;

public class Totem_of_Motion extends Skill {

    public static String NAME = "Totem of Motion";

    public Totem_of_Motion() {
        super();
        setDescription("เมื่อไม่มีหลักมนตราอื่นในสนาม ปลุกหลักแห่งศกราขึ้นมาในสนาม หลักมีพลังชีวิต 80 หน่วย หลักศกรามอบ 1 Action ให้กับยูนิตพันธมิตรทั้งหมดในสนาม");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(12);
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
