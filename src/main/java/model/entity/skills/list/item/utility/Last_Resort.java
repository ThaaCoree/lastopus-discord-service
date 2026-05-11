package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.ConditionTierType;
import main.java.model.type.ConditionType;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

public class Last_Resort extends Skill implements SkillWithCondition {

    public static String NAME = "Last Resort";

    public Last_Resort() {
        super();
        setDescription("เมื่อเหลือพลังชีวิตน้อยกว่า XA จากสูงสุด โดยพลังชีวิตสูงสุดมีมากกว่า XB หน่วย\n" +
                "ได้รับสถานะ Last Resort ซึ่งมอบ MATK XC");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.05"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.5*HP"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XC",new SkillMultiplier("1.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
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
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Last Resort");
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XC").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);

        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        if (getUser().getHealth().getUsable() <= xb) return;
        if (getUser().getHealth().getRemaining() <= getUser().getHealth().getUsable()*xa) {
            ConditionManager.applyCondition(condition, getUser(), 99);
        } else {
            ConditionManager.removeCondition(getUser(),"Last Resort");
        }

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
