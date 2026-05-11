package model.entity.skills.list.item.offensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Enders_Calling extends Skill {

    public static String NAME = "Ender's Calling";

    public Enders_Calling() {
        super();
        setDescription("เมื่อใช้งาน การจู่โจมครั้งถัดไปจะสร้างความเสียหายสามเท่า\n" +
                "เมื่อจู่โจมครั้งดังกล่าวเสร็จสิ้น หมดสภาพต่อสู้และไม่รับการฟื้นฟูจนกว่าจะจบรอบเทิร์นนี้");
        setActionType("Reaction");
        setManaCost(0);
        setCooldown(6);
        getPureTags().add(SkillType.SPELL);
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
        Conditions condition = new Conditions("Ender's Calling");
        condition.setDescription("การสร้างความเสียหายครั้งถัดไปจะสร้างความเสียหายสามเท่า จากนั้นหมดสภาพต่อสู้และไม่รับการฟื้นฟูจนกว่าจะจบรอบเทิร์นนี้");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.BOUND);

        addConditionToDatabase(condition, combatFlow);

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), getUser())
                        .condition(condition, 20)
                        .addActType(ActType.CONDITION_GIVEN, ActType.CAST)
                        .build()
        );
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, -1, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE) || event.unit_source != getUser()) return;
            if (!event.unit_source.hasCondition("Ender's Calling")) return;
            ConditionManager.removeCondition(event.unit_source, "Ender's Calling");
            event.addAllDamageMultModifier(2);

            Conditions condition = new Conditions("Ender's Demise");
            condition.setDescription("หมดสภาพต่อสู้และไม่รับการฟื้นฟูจนกว่าจะจบรอบเทิร์นนี้");
            condition.setConditionType(ConditionType.NEUTRAL);
            condition.setConditionTierType(ConditionTierType.BOUND);

            event.unit_source.setRemainingHealth(0);
            LogWriterUtil.log(event.unit_source.getName()+" fainted from Ender's Calling effect");

            sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), getUser())
                                        .condition(condition, 1)
                                        .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                        .build()
                        );
        });

        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.HEALTH_RECOVER) || event.unit_target.contains(getUser())) return;
            for (Unit unit : event.unit_target) {
                if (!event.unit_source.hasCondition("Ender's Demise")) return;
                event.addMultModifier(ActionEffectType.HEALTH_RECOVER, 0);
                LogWriterUtil.log("Health Recovery blocked by Ender's Demise");
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
