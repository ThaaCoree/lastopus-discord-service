package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Constellation_Seal extends Skill implements SkillWithCondition {

    public static String NAME = "Constellation Seal";

    public Constellation_Seal() {
        super();
        setDescription("เลือกศัตรู XA เป้าหมาย ผนึกพวกมันเป็นเวลา XB เทิร์น ยูนิตที่ถูกผนึกไม่สามารถใช้งาน Action ได้, มี ATK, DEF, Block, HealAMP, BuffAMP, DebuffAMP เป็น 0");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("1"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
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

            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Constellation Seal");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Constellation Seal");
        condition.getStatModifiers(StatType.PHYSICALATTACK).setOverride(0);
        condition.getStatModifiers(StatType.MAGICALATTACK).setOverride(0);
        condition.getStatModifiers(StatType.RANGEDATTACK).setOverride(0);
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setOverride(0);
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setOverride(0);
        condition.getStatModifiers(StatType.PHYSICALBLOCK).setOverride(0);
        condition.getStatModifiers(StatType.MAGICALBLOCK).setOverride(0);
        condition.getStatModifiers(StatType.HEALAMPLIFIER).setOverride(0);
        condition.getStatModifiers(StatType.BUFFAMPLIFIER).setOverride(0);
        condition.getStatModifiers(StatType.DEBUFFAMPLIFIER).setOverride(0);

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

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
