package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

public class Paper_Fortress extends Skill {

    public static String NAME = "Paper Fortress";

    public Paper_Fortress() {
        super();
        setDescription("ความเสียทุกชนิดที่รับไม่สามารถฝ่า Debris ได้, Lisa จะมี Debris ไม่ต่ำกว่า XA หน่วย\n" +
                "มี PDEF และ MDEF มากขึ้น XB");
        setActionType("Passive");

        getSkillMultiplier().put("XA",new SkillMultiplier("1.3*MP*(1+(ManaRegen/100))"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.75"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.FIGHTING_STYLE);
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
        if (!getIsActive()) return;
        double oldDebris = getUser().getDebris().getRemaining();
        if (oldDebris < getSkillMultiplier().get("XA").getResult()) {
            getUser().getDebris().setRemaining(getSkillMultiplier().get("XA").getResult());
        }

        double health = getUser().getStats().get(StatType.HEALTHPOINT).getFinal();
        double reserve = getUser().getStats().get(StatType.RESERVATION).getFinal();
        setHealthReserveFlat((health-1)/reserve);
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
