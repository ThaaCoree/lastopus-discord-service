package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;

public class Memories_Rewrite extends Skill {

    public static String NAME = "Memories Rewrite";

    public Memories_Rewrite() {
        super();
        setDescription("เขียนทับความทรงจำของเป้าหมาย สกิลที่เป้าหมายกำลังจะใช้งานถูกหยุดยั้งโดยสิ้นเชิง เป้าหมายนับคูลดาวน์และสูญเสียมานาในการใช้สกิลนั้น");
        setActionType("Reaction");
        setManaCost(11);
        setCooldown(4);
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
