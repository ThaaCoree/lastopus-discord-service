package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Knife_Draw extends Skill implements SkillWithCondition {

    public static String NAME = "Knife Draw";

    public Knife_Draw() {
        super();
        setDescription("หากมีดปักอยู่ที่เป้าหมาย กระชากมีดกลับมา สร้างความเสียหายกายภาพ XA หน่วยให้กับเป้าหมาย และมอบสถานะ Vital Cut ให้เป็นเวลา XB รอบเทิร์นด้วย\n" +
                "Vital Cut : ลด Status ทั้งหมดลง XC");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.1*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);

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

            Conditions condition = new Conditions("Vital Cut");
            double xc = getSkillMultiplier().get("XC").getResult() * -1;
            condition.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(xc);
            condition.getStatusModifiers(StatusType.AGILITY).setGlobalMult(xc);
            condition.getStatusModifiers(StatusType.VITALITY).setGlobalMult(xc);
            condition.getStatusModifiers(StatusType.DEXTERITY).setGlobalMult(xc);
            condition.getStatusModifiers(StatusType.WISDOM).setGlobalMult(xc);
            condition.getStatusModifiers(StatusType.INTELLIGENCE).setGlobalMult(xc);
            condition.getStatusModifiers(StatusType.LUCK).setGlobalMult(xc);

            condition.setConditionType(ConditionType.DEBUFF);
            condition.setConditionTierType(ConditionTierType.GENERAL);

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.ATTACK, ActType.STRIKE, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {

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
