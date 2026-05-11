package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

public class Singularity_Collapse extends Skill {

    public static String NAME = "Singularity, Collapse";

    public Singularity_Collapse() {
        super();
        setDescription("สร้างจุดเอกฐานขึ้นในพื้นที่ 3x3 เมตร หยุดยั้งทุกสิ่งที่กำลังเคลื่อนไหวอยู่ภายในเป็นเวลาหนึ่งเหตุการณ์ จากนั้นลบแรงออกจากวัตถุทั้งหมด");
        setActionType("Reaction");
        setManaCost(10);
        setCooldown(3);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.FIELD);
        setManaReservePercent(0.1);
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
