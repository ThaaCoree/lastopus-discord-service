package model.entity.skills.list.item.offensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;

public class Flame_Explode extends Skill {

    public static String NAME = "Flame Explode";

    public Flame_Explode() {
        super();
        setDescription("การโจมตีด้วยอาวุธระยะไกล จะมีระยะไกลสุดอยู่ที่ XA เมตร การโจมตีจะกว้างออกทางซ้ายและขวาด้านละหนึ่งเมตรเป็นพื้นที่\n" +
                "การโจมตีกลายเป็นธาตุไฟ");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getPureTags().add(SkillType.FIRE);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISTANCE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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
