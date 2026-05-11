package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import model.type.StatType;

public class Luminous extends Skill {

    public static String NAME = "Luminous";

    public Luminous() {
        super();
        setDescription("เพิ่ม ATK ตาม XA ของ HealAMP");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);
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
        double healAMP = getUser().getStats().get(StatType.HEALAMPLIFIER).getFinal();
        double xa = getSkillMultiplier().get("XA").getResult();
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setGlobalMult(healAMP*xa);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(healAMP*xa);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(healAMP*xa);
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
