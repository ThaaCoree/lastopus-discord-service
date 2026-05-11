package model.entity.skills.list.item.support;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import model.type.StatType;

public class Hope_And_Insanity extends Skill {

    public static String NAME = "Hope & Insanity";

    public Hope_And_Insanity() {
        super();
        setDescription("ได้รับ XA ATK ตามพลังชีวิตที่น้อยลงทุกๆ XB หน่วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.07"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.09*HP"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.REQUIREMENT);
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
        double remainHP = getUser().getHealth().getRemaining();
        double maxHP = getUser().getStats().get(StatType.HEALTHPOINT).getFinal();
        double lostHP = maxHP-remainHP;
        int multiplies = (int) ( lostHP / getSkillMultiplier().get("XB").getResult() );
        double xa = getSkillMultiplier().get("XA").getResult();
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setGlobalMult(xa*multiplies);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(xa*multiplies);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(xa*multiplies);
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
