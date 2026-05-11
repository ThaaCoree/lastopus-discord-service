package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Stalker_Pounce extends Skill implements SkillWithCondition {

    public static String NAME = "Stalker Pounce";

    public Stalker_Pounce() {
        super();
        setDescription("กระโดดพุ่งตัว หลบการจู่โจมหนึ่งครั้งอย่างแน่นอน จากนั้นเพิ่ม Evasion XA เป็นเวลา XB รอบเทิร์น");
        setActionType("Reaction");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.82*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB", new SkillMultiplier("1"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
        if (!skillTarget.getTarget(0).isEmpty()) {

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Stalker Pounce");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions condition = new Conditions("Stalker Pounce");
        condition.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XA").getResult() / buffAMP);

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);

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
