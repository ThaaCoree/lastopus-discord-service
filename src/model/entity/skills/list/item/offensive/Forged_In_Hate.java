package model.entity.skills.list.item.offensive;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;

public class Forged_In_Hate extends Skill {

    public static String NAME = "Forged in Hate";

    public Forged_In_Hate() {
        super();
        setDescription("เมื่อจู่โจมคริติคอล สามารถจู่โจมได้อีกหนึ่งชุด\n" +
                "การใช้งานความสามารถนี้จะทำให้สูญเสียพลังชีวิต XA หน่วย\n" +
                "และสูญเสียมากขึ้นอีก XA หน่วยทุกครั้งที่สั่งใช้งานต่อเนื่องในรอบเทิร์นนี้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.STRIKE);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.25*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DRAWBACK);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        setHealthCost(xa);
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
