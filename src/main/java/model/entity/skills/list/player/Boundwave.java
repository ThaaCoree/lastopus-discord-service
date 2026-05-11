package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

public class Boundwave extends Skill {

    public static String NAME = "Boundwave";

    public Boundwave() {
        super();
        setDescription("ได้รับ Evasion และ Block เพิ่มขึ้นอีก XA หน่วย และ XB หน่วยตามลำดับ\n" +
                "สแตทที่ได้รับจากสกิลนี้เพิ่มขึ้นจาก LUK");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.25);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.7*LUK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.45*LUK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEFENSE);
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
        getSkillModifier().getStatModifierSafe(StatType.EVASION).setFlat(xa);
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALBLOCK).setFlat(xb);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALBLOCK).setFlat(xb);
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
