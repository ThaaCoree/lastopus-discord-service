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

public class Dominating_Treads extends Skill implements SkillWithCondition {

    public static String NAME = "Dominating Treads";

    public Dominating_Treads() {
        super();
        setDescription("เมื่อโจมตีระยะใกล้ ได้รับ RATK XA\n" +
                "เมื่อโจมตีระยะไกล ได้รับ PATK XB\n" +
                "หากยังไม่ได้โจมตีเลย ได้รับ RATK ก่อน");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (getUser().hasCondition("Dominating Treads (RATK)")) {
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Dominating Treads (PATK)");
            ConditionManager.applyCondition(condition, getUser(), 99);
            ConditionManager.removeCondition(getUser(), "Dominating Treads (RATK)");
        } else {
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Dominating Treads (RATK)");
            ConditionManager.applyCondition(condition, getUser(), 99);
            ConditionManager.removeCondition(getUser(), "Dominating Treads (PATK)");
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Dominating Treads (RATK)");
        Conditions condition2 = new Conditions("Dominating Treads (PATK)");
        condition.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition2.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(getSkillMultiplier().get("XB").getResult());

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);
        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.UNDISPELLABLE);

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
