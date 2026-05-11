package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;

public class Collision_Tear extends Skill {

    public static String NAME = "Collision Tear";

    public Collision_Tear() {
        super();
        setDescription("สร้างรอยแยกขึ้นในอากาศ กว้างสุด XA เมตร ยาวสุด XB เมตร\n" +
                "สิ่งที่อยู่ในรอยแยกนี้จะถูกผลักออก และทั้งสองด้านของรอยแยกจะถูกขว้างกั้นด้วยสิ่งที่มองไม่เห็น");
        setActionType("Combine");
        setManaCost(6);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("6"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DISTANCE);
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
