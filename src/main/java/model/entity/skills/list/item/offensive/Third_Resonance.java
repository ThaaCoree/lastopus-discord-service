package model.entity.skills.list.item.offensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class Third_Resonance extends Skill {

    public static String NAME = "Third Resonance";

    public Third_Resonance() {
        super();
        setDescription("การร่ายเวทมนตร์ที่สร้างความเสียหายได้ จะร่ายครั้งที่สองและสาม\n" +
                "โดยในครั้งที่สองจะใช้มานามากขึ้นสามเท่า และในการร่ายครั้งที่สามจะใช้งาน HP เป็นสิบเท่าของมานาที่ใช้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(1);
        getPureTags().add(SkillType.SPELL);
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
