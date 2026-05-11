package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

public class Destiny_Avoidance extends Skill {

    public static String NAME = "Destiny Avoidance";

    public Destiny_Avoidance() {
        super();
        setDescription("มองเห็นความเสียหายที่มากที่สุดที่จะเกิดขึ้นกับพันธมิตรในเทิร์นหน้า หลบเลี่ยงความเสียหายนั้นได้อย่างแน่นอน");
        setActionType("Action");
        setManaCost(8);
        setCooldown(4);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.OPUS);
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
