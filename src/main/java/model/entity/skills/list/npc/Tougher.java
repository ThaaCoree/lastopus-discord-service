package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;
import main.java.model.type.StatusType;

public class Tougher extends Skill {

    public static String NAME = "TOUGHERRRR!!!";

    public Tougher() {
        super();
        setDescription("ได้รับ PDEF และ MDEF จาก VIT มากขึ้น XA");
        setActionType("Passive");
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.8*(1+WIS/100)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
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
//        , 0, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        double xa = getSkillMultiplier().get("XA").getResult();
        double vit = getUser().getStatuses().get(StatusType.VITALITY).getFinal();
        getSkillModifier().getStatModifierSafe(StatType.MAGICALDEFENSE).setFlat(vit*1.5*xa);
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALDEFENSE).setFlat(vit*1.5*xa);
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
