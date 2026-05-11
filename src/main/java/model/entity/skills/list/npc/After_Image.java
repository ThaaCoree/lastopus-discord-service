package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class After_Image extends Skill implements SkillWithCondition {

    public static String NAME = "After Image";

    public After_Image() {
        super();
        setDescription("หนึ่งครั้งต่อรอบเทิร์น ใช้งาน Combined Action เพื่อทำให้ Speed ของพันธมิตรหนึ่งคนมีค่าเท่ากับตนเองจนกว่าจะจบการต่อสู้, ยูนิตดังกล่าวได้รับ Action เพิ่มเติม 1 หน่วยต่อรอบเทิร์นด้วย\n" +
                "ใช้งานสกิลนี้อีกครั้งเพื่อเปลี่ยนยูนิตที่ได้รับผล");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.FIGHTING_STYLE);
        getPureTags().add(SkillType.OPUS);
        setManaReservePercent(0.1);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 0)
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
        combatFlow.getAllUnit().forEach((name, unit) -> {
            if (unit.hasCondition("After Image")) {
                ConditionManager.removeCondition(unit, "After Image");
            }
        });
        if (!skillTarget.getTarget(0).isEmpty()) {
            int duration = 99;

            Conditions condition = combatFlow.findCondition("After Image");

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("After Image");
        condition.setDescription("ได้รับ Action เพิ่มเติม 1 หน่วยต่อรอบเทิร์น");
        condition.getStatModifiers(StatType.SPEED).setOverride(getUser().getStats().get(StatType.SPEED).getFinal() - 0.001);

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
//        EventBus eventBus = combatFlow.getEventBus();
//        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
//            if (!event.hasActType(ActType.HEAL) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
//            List<Unit> targets = event.unit_target;
//            double heal_amount = event.getHeal();
//
//            sendActionEvent(combatFlow.getEventBus(),
//                                ActionEvent.builder(getName(), getUser(), targets)
//                                        .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                                        .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                                        .build()
//                        );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
