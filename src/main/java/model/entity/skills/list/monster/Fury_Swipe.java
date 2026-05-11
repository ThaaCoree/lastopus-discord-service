package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Fury_Swipe extends Skill implements SkillWithCondition {

    public static String NAME = "Fury Swipe";

    public Fury_Swipe() {
        super();
        setDescription("ข่วนโจมตี XB ครั้งใส่เป้าหมาย สร้างความเสียหายกายภาพครั้งละ XA หน่วย\n" +
                "หากเป้าหมายพยายามหลบ มอบสถานะ Blood Drip เป็นเวลา XC รอบเทิร์นด้วย\n" +
                "Blood Drip : ลด VIT และ STR ลง XD");
        setActionType("Action");
        setManaCost(0);
        setCooldown(1);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.8*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);

        getSkillMultiplier().put("XB",new SkillMultiplier("6"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XC",new SkillMultiplier("3"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.35*(1+DebuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XD").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Dodge?", SkillInputSpec.InputType.BOOLEAN, 0)
        , 0, 0);
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
            int duration = (int) getSkillMultiplier().get("XC").getResult();
            double xa = getSkillMultiplier().get("XA").getResult();
            double xb = getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.findCondition("Blood Drip");

            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);
                if (skillTarget.getDecision(name, 0, 0).contains("TRUE")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, (int) xb)
                                    .condition(condition, duration)
                                    .addActType(ActType.ATTACK, ActType.CONDITION_GIVEN)
                                    .build());
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, (int) xb)
                                    .addActType(ActType.ATTACK)
                                    .build());
                }
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Blood Drip");
        double xd = getSkillMultiplier().get("XD").getResult();
        condition.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(xd*-1);
        condition.getStatusModifiers(StatusType.VITALITY).setGlobalMult(xd*-1);

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        addConditionToDatabase(condition, combatFlow);
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
