package model.entity.skills.list.item.defensive;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEffect;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillWithCondition;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Mist_Form extends Skill implements SkillWithCondition {

    public static String NAME = "Mist Form";

    public Mist_Form() {
        super();
        setDescription("เมื่อจู่โจมจากจุดบอดหรือสเตลท์สำเร็จ ไม่รับความเสียหายกายภาพจนกว่าจะจบรอบเทิร์นนี้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.ETHEREAL);
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
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Fatima");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Mist Form");
        condition.setDescription("ไม่รับความเสียหายกายภาพ");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, -1, (ActionEvent event) -> {
            if (!event.hasTarget(getUser().getName()) || event.hasPhysicalDamage(getUser().getName()) || !getUser().hasCondition("Mist Form")) return;
            for (Unit unit : event.unit_target) {
                for (ActionEffect actionEffect : event.effects.get(unit.getName())) {
                    if (actionEffect.type == ActionEffectType.DAMAGE_PHYSICAL) {
                        actionEffect.finalValue = 0;
                        LogWriterUtil.log("Mist Form triggered");
                    }
                }
            }

            sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), getUser())
                                        .addActType(ActType.SKILL_TRIGGER)
                                        .build()
                        );
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
