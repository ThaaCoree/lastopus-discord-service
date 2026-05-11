package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.modifier.TransferModifier;
import main.java.model.type.*;

public class Reverse_Sanity extends Skill implements SkillWithCondition {

    public static String NAME = "Reverse Sanity";

    public Reverse_Sanity() {
        super();
        setDescription("มอบสถานะ Reverse Polarity ให้กับเป้าหมาย สลับ PDEF และ MDEF ของเป้าหมาย เป็นเวลา XA เทิร์น");
        setActionType("Action");
        setManaCost(8);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);
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
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Reverse Polarity");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Reverse Polarity");

        TransferModifier modifier = new TransferModifier(StatType.MAGICALDEFENSE, StatType.PHYSICALDEFENSE, null, null, 1);
        TransferModifier modifier2 = new TransferModifier(StatType.MAGICALDEFENSE, StatType.PHYSICALDEFENSE, null, null, 1);

        modifier.setTransferType(TransferType.CONVERSION);
        modifier2.setTransferType(TransferType.CONVERSION);

        condition.getTransferModifiers().put(1,modifier);
        condition.getTransferModifiers().put(2,modifier2);

        condition.setConditionType(ConditionType.NEUTRAL);
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
