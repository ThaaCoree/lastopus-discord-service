package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import model.type.StatType;

public class Twilight extends Skill {

    public static String NAME = "Twilight";

    public Twilight() {
        super();
        setDescription("ลดการสำรองมานาและพลังชีวิต XA เพิ่ม ManaRegen XB");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.2);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.25"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.RESOURCE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.25*(1+INT/100)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.RESOURCE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").setPercent(true);

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
        double xb = getSkillMultiplier().get("XB").getResult();
        getSkillModifier().getStatModifierSafe(StatType.RESERVATION).setFlat(xa*(-1));
        getSkillModifier().getStatModifierSafe(StatType.MANAREGEN).setGlobalMult(xb);
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
