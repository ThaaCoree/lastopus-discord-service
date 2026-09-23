package model.entity.skills.list.item.support;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Devotion extends Skill implements SkillWithCondition {

    public static String NAME = "Devotion";

    public Devotion() {
        super();
        setDescription("เลือกยูนิตที่ไม่ใช่สิ่งอัญเชิญหนึ่งเป้าหมาย รับความเสียหายแทนยูนิตเป้าหมายทั้งหมดเป็นเวลา 1 รอบเทิร์น");
        setActionType("Action");
        setManaCost(0);
        setCooldown(1);
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
            Conditions condition = combatFlow.findCondition("Devoted");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, 1)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Devoted");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, -5, (ResourceEvent event) -> {
            if (!event.target.hasCondition("Devoted")) return;
            if (!event.isDamage()) return;

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), event.target)
                            .addActType(ActType.SKILL_TRIGGER)
                            .build()
            );

            event.target = getUser();
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
