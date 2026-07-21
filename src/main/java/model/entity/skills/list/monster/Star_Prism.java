package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Star_Prism extends Skill implements SkillWithCondition {

    public static String NAME = "Star Prism";

    public Star_Prism() {
        super();
        setDescription("มาร์กเป้าหมายให้เป็น Reflection Point นาน XA รอบเทิร์น, ในระหว่างนี้ หากยูนิตพันธมิตรของเป้าหมายได้รับความเสียหาย เป้าหมายได้รับความเสียหายจริงในจำนวนเท่ากันด้วย");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(3);
        getPureTags().add(SkillType.PHYSICAL);
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
            int duration = (int) getSkillMultiplier().get("XA").getResult();
            Conditions condition = combatFlow.findCondition("Reflection Point");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Reflection Point");
        condition.setDescription("เมื่อพันธมิตรได้รับความเสียหาย ยูนิตนี้ได้รับความเสียหายเท่ากันด้วย");

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
            if (event.event_source.equalsIgnoreCase(getName())) return;
            List<Unit> targets = event.unit_target;
            for (Unit target : targets) {
                double damage = event.getDamage(target.getName());

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), targets)
                                .effect(ActionEffectType.DAMAGE_TRUE,damage, 1)
                                .addActType(ActType.STRIKE, ActType.SKILL_TRIGGER)
                                .build()
                );
            }

        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
