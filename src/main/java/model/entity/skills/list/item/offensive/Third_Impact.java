package model.entity.skills.list.item.offensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class Third_Impact extends Skill {

    public static String NAME = "Third Impact";

    public Third_Impact() {
        super();
        setDescription("เมื่อสร้างความเสียหายด้วยเวทมนตร์ครบสองครั้ง ในการสร้างความเสียหายครั้งที่สาม จะสร้างความเสียหายครั้งที่สี่เป็นครึ่งหนึ่งของความเสียหายนั้นด้วย");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.COMBO);
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
