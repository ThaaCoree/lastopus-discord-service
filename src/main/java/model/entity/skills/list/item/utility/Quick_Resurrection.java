package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class Quick_Resurrection extends Skill {

    public static String NAME = "Quick Resurrection";

    public Quick_Resurrection() {
        super();
        setDescription("ปลุกยูนิตที่ตายแล้วขึ้นมาด้วยพลังชีวิตครึ่งหนึ่ง มันรับความเสียหายเป็นสองเท่า สร้างความเสียหายครึ่งหนึ่ง และตายเมื่อสิ้นสุดการต่อสู้");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.SPELL);
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
