package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

public class New_Moon_Lunar extends Skill {

    public static String NAME = "New Moon Lunar Reversal";

    public New_Moon_Lunar() {
        super();
        setDescription("เดี่ยว : ยกดาบขึ้นตั้งกระบวนท่าปัดป้องและสวนกลับ หากถูกจู่โจมในระหว่างนี้ สามารถหลบหรือบล็อก แล้วโจมตีสวนกลับได้ การโจมตีนี้สามารถเป็นสกิลได้\n" +
                "คู่ : ทั้งสองร่างตั้งกระบวนท่า");
        setActionType("Reaction");
        setManaCost(4);
        setCooldown(2);
        getPureTags().add(SkillType.PHYSICAL);
        getPureTags().add(SkillType.STRIKE);
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
