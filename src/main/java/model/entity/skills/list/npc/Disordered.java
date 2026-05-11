package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.modifier.TransferModifier;
import model.type.*;

public class Disordered extends Skill implements SkillWithCondition {

    public static String NAME = "Disordered";

    public Disordered() {
        super();
        setDescription("เลือกหนึ่งเป้าหมาย มอบสถานะ Disordered ให้กับมันเป็นเวลา XA เทิร์น\n" +
                "Disordered : Convert 100% ของ PATK, RATK, MATK ไปเป็น RATK, MATK, PATK ตามลำดับ");
        setActionType("Action");
        setManaCost(8);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
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
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Disordered");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Disordered");

        TransferModifier conv1 = new TransferModifier(StatType.PHYSICALATTACK, StatType.RANGEDATTACK, null, null, 1);
        TransferModifier conv2 = new TransferModifier(StatType.RANGEDATTACK, StatType.MAGICALATTACK, null, null, 1);
        TransferModifier conv3 = new TransferModifier(StatType.MAGICALATTACK, StatType.PHYSICALATTACK, null, null, 1);

        conv1.setTransferType(TransferType.CONVERSION);
        conv2.setTransferType(TransferType.CONVERSION);
        conv3.setTransferType(TransferType.CONVERSION);

        condition.getTransferModifiers().put(1, conv1);
        condition.getTransferModifiers().put(2, conv2);
        condition.getTransferModifiers().put(3, conv3);

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
