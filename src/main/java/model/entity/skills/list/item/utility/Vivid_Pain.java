package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

public class Vivid_Pain extends Skill {

    public static String NAME = "Vivid Pain";

    public Vivid_Pain() {
        super();
        setDescription("เมื่อมีพลังชีวิตต่ำกว่า XA ได้รับ ManaRegen เพิ่มขึ้นอีกเท่าตัว");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);
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
        double currentHP = getUser().getHealth().getRemaining();
        double usableHP = getUser().getHealth().getUsable();
        double xa = getSkillMultiplier().get("XA").getResult();
        if (currentHP <= usableHP*xa) {
            getSkillModifier().getStatModifierSafe(StatType.MANAREGEN).setGlobalMult(1);
        }
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
