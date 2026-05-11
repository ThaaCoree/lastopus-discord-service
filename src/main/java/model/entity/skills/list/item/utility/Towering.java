package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import model.type.StatType;

public class Towering extends Skill {

    public static String NAME = "Towering";

    public Towering() {
        super();
        setDescription("แปลง MSPD ที่เกิน 2 ไปเป็น RATK โดยจะได้รับ RATK XA หน่วยต่อ MSPD 1 หน่วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.02*(1+DEX/25)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
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
        double mspd = getUser().getStats().get(StatType.MOVEMENTSPEED).getFinal();
        double difference = mspd-2;
        if (difference > 0) {
            getSkillModifier().getStatModifierSafe(StatType.MOVEMENTSPEED).setOverride(1);
            getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(difference*xa);
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
