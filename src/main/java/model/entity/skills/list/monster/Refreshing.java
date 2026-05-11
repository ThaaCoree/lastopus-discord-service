package model.entity.skills.list.monster;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillInstance;

import java.util.Map;

public class Refreshing extends Skill {

    public static String NAME = "Refreshing";

    public Refreshing() {
        super();
        setDescription("ใช้งานได้โดยเฉพาะหัว Levanis เท่านั้น" +
                "ลดคูลดาวน์ของสกิลทั้งหมดลงหนึ่งเทิร์น");
        setActionType("Action");
        setManaCost(0);
        setCooldown(2);
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
        for (Map.Entry<String, SkillInstance> entry : getUser().getAllSkill().entrySet()) {
            int old_cd = entry.getValue().getOnCooldown();
            entry.getValue().setOnCooldown(old_cd-1);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
