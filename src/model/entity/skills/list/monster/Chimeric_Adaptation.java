package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEffect;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Chimeric_Adaptation extends Skill {

    public static String NAME = "Chimeric Adaptation";

    public Chimeric_Adaptation() {
        super();
        setDescription("เมื่อได้รับความเสียหายกายภาพ การจู่โจมทั้งหมดจะสร้างความเสียหายเวท\n" +
                "เมื่อได้รับความเสียหายเวท การจู่โจมทั้งหมดจะสร้างความเสียหายกายภาพ\n" +
                "เมื่อได้รับความเสียหายโดยตรง การจู่โจมทั้งหมดจะสร้างความเสียหายโดยตรง");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
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
        int duration = 99;


        Conditions physical = new Conditions("Chimeric Adapation (Physical)");
        physical.setDescription("การจู่โจมทั้งหมดสร้างความเสียหายกายภาพ");
        physical.setConditionType(ConditionType.NEUTRAL);
        physical.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(physical, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (event.target != getUser()) return;

            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL)) {

                Conditions condition = new Conditions("Chimeric Adaptation (Physical)");
                condition.setDescription("การจู่โจมทั้งหมดสร้างความเสียหายกายภาพ");
                condition.setConditionType(ConditionType.NEUTRAL);
                condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

                ConditionManager.removeCondition(event.target, "Chimeric Adaptation (Magical)");
                ConditionManager.removeCondition(event.target, "Chimeric Adaptation (Pure)");
                ConditionManager.applyCondition(condition, event.target, event.target, 99);

                sendSkillTriggerEvent(combatFlow, "Chimeric Adaptation triggered (physical)");
            }

            if (event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL)) {

                Conditions condition = new Conditions("Chimeric Adaptation (Magical)");
                condition.setDescription("การจู่โจมทั้งหมดสร้างความเสียหายเวท");
                condition.setConditionType(ConditionType.NEUTRAL);
                condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

                ConditionManager.removeCondition(event.target, "Chimeric Adaptation (Physical)");
                ConditionManager.removeCondition(event.target, "Chimeric Adaptation (Pure)");
                ConditionManager.applyCondition(condition, event.target, event.target, 99);

                sendSkillTriggerEvent(combatFlow, "Chimeric Adaptation triggered (magical)");
            }

            if (event.effectType.equals(ActionEffectType.DAMAGE_PURE)) {

                Conditions condition = new Conditions("Chimeric Adaptation (Pure)");
                condition.setDescription("การจู่โจมทั้งหมดสร้างความเสียหายโดยตรง");
                condition.setConditionType(ConditionType.NEUTRAL);
                condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

                ConditionManager.removeCondition(event.target, "Chimeric Adaptation (Magical)");
                ConditionManager.removeCondition(event.target, "Chimeric Adaptation (Physical)");
                ConditionManager.applyCondition(condition, event.target, event.target, 99);

                sendSkillTriggerEvent(combatFlow, "Chimeric Adaptation triggered (pure)");
            }
        });

        eventBus.register(ActionEvent.class, EventPhase.MODIFY, -5, event -> {
            if (event.damage_times <= 0) return;
            if (event.unit_source != getUser()) return;

            for (Unit target : event.unit_target) {
                double damage = event.getDamage(target.getName());

                Unit user = event.unit_source;

                if (user.hasCondition("Chimeric Adaptation (Physical)") ||
                        user.hasCondition("Chimeric Adaptation (Magical)") ||
                        user.hasCondition("Chimeric Adaptation (Pure)")) {

                    event.effects.forEach((name, list) -> {
                        for (ActionEffect actionEffect : list) {
                            actionEffect.finalValue = 0;
                        }
                    });
                }

                if (user.hasCondition("Chimeric Adaptation (Physical)")) {
                    event.doDamage(DamageType.PHYSICAL, damage);
                } else if (user.hasCondition("Chimeric Adaptation (Magical)")) {
                    event.doDamage(DamageType.MAGICAL, damage);
                } else if (user.hasCondition("Chimeric Adaptation (Pure)")) {
                    event.doDamage(DamageType.PURE, damage);
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
