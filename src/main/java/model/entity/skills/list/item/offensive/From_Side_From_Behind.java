package model.entity.skills.list.item.offensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class From_Side_From_Behind extends Skill {

    public static String NAME = "From Side, From Behind";

    public From_Side_From_Behind() {
        super();
        setDescription("การจู่โจมต่อเนื่องจากพันธมิตรในมุมด้านข้างหรือด้านหลังเป้าหมาย จะกลายเป็นสเตลท์\n" +
                "พันธมิตรที่จู่โจมต่อเนื่องจากผู้ใช้ในมุมด้านข้างหรือด้านหลังเป้าหมาย จะกลายเป็นสเตลท์");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getPureTags().add(SkillType.STRIKE);
        getPureTags().add(SkillType.STEALTH);
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
