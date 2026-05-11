package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Istenta extends Skill implements SkillWithCondition {

    public static String NAME = "Istenta";

    public Istenta() {
        super();
        setDescription("มอบสถานะ Istenta ให้กับเป้าหมายเป็นเวลา XB รอบเทิร์น ซึ่งเพิ่ม ATK และ DEF XA\n" +
                "ทุกครั้งที่เป้าหมายได้รับฮีล ระยะเวลาของ Istenta จะเพิ่มขึ้นหนึ่งรอบเทิร์น");
        setActionType("Combine");
        setManaCost(11);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
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
//        , 0, 0);
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
            Conditions condition = combatFlow.findCondition("Istenta");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Istenta");
        double xa = getSkillMultiplier().get("XA").getResult();
        condition.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(xa);
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(xa);
        condition.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(xa);
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(xa);
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(xa);

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.HEAL)) return;

            for (Unit unit : event.unit_target) {
                if (!unit.hasCondition("Istenta")) continue;
                for (ConditionInstance instance : unit.getConditionInstances().values()) {
                    if (instance.getCondition().getName().equals("Istenta")) {
                        double duration_remain = instance.getDurationRemain();
                        instance.setDurationRemain((int) duration_remain+event.heal_times);
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
