package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Refinery extends Skill implements SkillWithCondition {

    public static String NAME = "Refinery";

    public Refinery() {
        super();
        setDescription("ใช้งาน 1 Malice Point, มอบสถานะ Dentel Shell ให้กับเป้าหมายเป็นเวลา XC รอบเทิร์น\n" +
                "Dentel Shell : เพิ่ม DEF XA หน่วย และ Block XB หน่วยให้กับเป้าหมาย\n" +
                "หากใช้งาน Malice Point มากกว่า 1 หน่วย จะทำให้ Dentel Shell ส่งผลมากขึ้น XD ต่อ Malice Point ที่สูญเสียไป");
        setActionType("Combine");
        setManaCost(4);
        setCooldown(1);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*VIT"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.5*VIT"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.5"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XD").setPercent(true);
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
        Conditions condition = combatFlow.findCondition("Fatima");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Fatima");
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XA").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

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
