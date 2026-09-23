package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Tricked extends Skill implements SkillWithCondition {

    public static String NAME = "Tricked";

    public Tricked() {
        super();
        setDescription("Passive : การจู่โจมจากภาพลวงตาที่อัญเชิญจะไม่สามารถถูกหลบได้\n" +
                "Active : มอบสถานะ Confused ให้กับเป้าหมายเป็นเวลา XB รอบเทิร์น\n" +
                "Confused : ลด Accuracy XA และ มีโอกาสโจมตีพลาดไปเป้าหมายที่ไม่ได้เลือก");
        setActionType("Action");
        setManaCost(5);
        setCooldown(2);
        setManaReservePercent(0.15);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.3*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
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
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.findCondition("Confused");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Confused");
        condition.getStatModifiers(StatType.ACCURACY).setGlobalMult(getSkillMultiplier().get("XA").getResult() * -1);

        condition.setConditionType(ConditionType.DEBUFF);
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
