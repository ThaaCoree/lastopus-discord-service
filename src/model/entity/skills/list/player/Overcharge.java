package model.entity.skills.list.player;

import javafx.beans.InvalidationListener;
import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.modifier.BasicModifier;
import model.type.*;

public class Overcharge extends Skill {

    public static String NAME = "Overcharge";

    public Overcharge() {
        super();
        setDescription("เมื่อใช้งานโอปัส จะได้รับ 1 Flicker Stack สูงสุด XB Stack\n" +
                "ได้รับ PATK XA หน่วยต่อ 1 Flicker Stack");
        setActionType("Passive");
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.08*STR + 0.1*AGI"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XB",new SkillMultiplier("16"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);

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
        if (getUser().getCounter() != null) {
            BasicModifier modifier = new BasicModifier();
            modifier.setFlat(getSkillMultiplier().get("XA").getResult() * getUser().getCounter().get(CounterName.FLICKER));
            getSkillModifier().getStatModifiers().put(StatType.PHYSICALATTACK, modifier);

            getUser().getCounter().addListener((InvalidationListener) change -> {
                getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setFlat(getSkillMultiplier().get("XA").getResult() * getUser().getCounter().get(CounterName.FLICKER));
                getUser().calculateStatAndStatus();
            });
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
