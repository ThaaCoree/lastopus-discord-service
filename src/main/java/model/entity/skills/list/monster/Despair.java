package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Despair extends Skill implements SkillWithCondition {

    public static String NAME = "Despair";

    public Despair() {
        super();
        setDescription("เมื่อเริ่มต้นรอบเทิร์น เลือกมอบหนึ่งสถานะต่อไปนี้ให้กับยูนิตศัตรูทั้งหมดจนกว่าจะจบรอบเทิร์นนี้\n" +
                "Despair-Fragile : ลด DMGReduction ลง -XA\n" +
                "Despair-Gentle : ลด DMGAmplifier ลง -XB\n" +
                "Despair-Shock : ลด AGI ลง XC\n" +
                "Despair-Fate : ลด LUK ลง XD\n" +
                "Despair-Confusion : เพิ่ม Reservation +XE");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.25*(1+DebuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.3*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.3*(1+DebuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XD").setPercent(true);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.2*(1+DebuffAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XE").setPercent(true);

    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Fragile", "Gentle", "Shock","Fate","Confusion");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
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
        Conditions condition = combatFlow.findCondition("Fragile");
        if (skillTarget.getTarget(0).contains("Fragile")) {
            condition = combatFlow.findCondition("Despair-Fragile");
        }
        if (skillTarget.getTarget(0).contains("Gentle")) {
            condition = combatFlow.findCondition("Despair-Gentle");
        }
        if (skillTarget.getTarget(0).contains("Shock")) {
            condition = combatFlow.findCondition("Despair-Shock");
        }
        if (skillTarget.getTarget(0).contains("Fate")) {
            condition = combatFlow.findCondition("Despair-Fate");
        }
        if (skillTarget.getTarget(0).contains("Confusion")) {
            condition = combatFlow.findCondition("Despair-Confusion");
        }

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), getEnemies(combatFlow))
                        .condition(condition, 1)
                        .addActType(ActType.SKILL_TRIGGER, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions fragile = new Conditions("Despair-Fragile");
        fragile.getStatModifiers(StatType.DAMAGEREDUCTION).setFlat(getSkillMultiplier().get("XA").getResult() * -1);
        fragile.setConditionType(ConditionType.DEBUFF);
        fragile.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions gentle = new Conditions("Despair-Gentle");
        gentle.getStatModifiers(StatType.DAMAGEAMPLIFIER).setFlat(getSkillMultiplier().get("XB").getResult() * -1);
        gentle.setConditionType(ConditionType.DEBUFF);
        gentle.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions shock = new Conditions("Despair-Shock");
        shock.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XC").getResult() * -1);
        shock.setConditionType(ConditionType.DEBUFF);
        shock.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions fate = new Conditions("Despair-Fate");
        fate.getStatusModifiers(StatusType.LUCK).setFlat(getSkillMultiplier().get("XD").getResult() * -1);
        fate.setConditionType(ConditionType.DEBUFF);
        fate.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions confusion = new Conditions("Despair-Confusion");
        confusion.getStatModifiers(StatType.RESERVATION).setFlat(getSkillMultiplier().get("XE").getResult());
        confusion.setConditionType(ConditionType.DEBUFF);
        confusion.setConditionTierType(ConditionTierType.ADVANCED);

        addConditionToDatabase(fragile, combatFlow);
        addConditionToDatabase(gentle, combatFlow);
        addConditionToDatabase(shock, combatFlow);
        addConditionToDatabase(fate, combatFlow);
        addConditionToDatabase(confusion, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(fragile, unit);
            ConditionManager.reapplyCondition(gentle, unit);
            ConditionManager.reapplyCondition(shock, unit);
            ConditionManager.reapplyCondition(fate, unit);
            ConditionManager.reapplyCondition(confusion, unit);
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
//                    ActionEvent.builder(getName(), getUser(), targets)
//                            .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                            .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                            .build()
//            );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
