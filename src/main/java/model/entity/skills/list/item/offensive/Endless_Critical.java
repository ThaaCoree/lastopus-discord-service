package main.java.model.entity.skills.list.item.offensive;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

public class Endless_Critical extends Skill {

    public static String NAME = "Endless Critical";

    public Endless_Critical() {
        super();
        setDescription("เมื่อจู่โจมเป็นคริติคอล สามารถยกเลิกความเสียหายที่กำลังจะสร้างเพิ่มเติม เพื่อจู่โจมเพิ่มอีกหนึ่งครั้งได้\n" +
                "การใช้งานความสามารถนี้ จะทำให้การจู่โจมครั้งถัดไปเพิ่มลูกเต๋าในการทอยคริติคอลอีกหนึ่งลูก แล้วเลือกลูกที่ต่ำที่สุด");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
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
