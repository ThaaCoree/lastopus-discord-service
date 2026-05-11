package model.entity.skills.list.item.defensive;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import model.entity.Conditions;
import model.entity.skills.*;
import model.type.*;

public class Forbidden_Rite extends Skill implements SkillWithCondition {

    public static String NAME = "Forbidden Rite";

    public Forbidden_Rite() {
        super();
        setDescription("สละพลังชีวิต XA หน่วย จนกว่าจะถึงรอบเทิร์นถัดไปจะสูญเสียพลังชีวิตได้อีกไม่เกิน XB หน่วย การรับความเสียหายระหว่างผลพิเศษนี้จะไม่ทำให้พลังชีวิตเหลือต่ำกว่า 1 หน่วย");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.SPELL);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DRAWBACK);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.3*UsableHP"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.RITUAL);
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
        Conditions condition = new Conditions("Forbidden Rite");
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        getUser().sumRemainingHealth(xa*(-1));
        double remainingHP = getUser().getHealth().getRemaining();

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        condition.setDescription("สูญเสียพลังชีวิตได้อีกไม่เกิน "+(xb)+" หน่วย การรับความเสียหายระหว่างผลพิเศษนี้จะไม่ทำให้พลังชีวิตต่ำกว่า 1 หน่วย");

        int duration = 1;
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition, duration, "DamageTakenRemain", xb)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, -2, (ResourceEvent event) -> {
            if (event.target != getUser()) return;
            if (!event.target.hasCondition("Forbidden Rite")) return;
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                var condition = event.target.findCondition("Forbidden Rite");
                double current_health = getUser().getHealth().getRemaining();
                double xb = condition.getNumberRecordOrDefault("DamageTakenRemain", 0);

                if (event.amount > xb) {
                    event.amount = xb;
                    condition.putNumberRecord("DamageTakenRemain", 0);
                    condition.getCondition().setDescription("สูญเสียพลังชีวิตได้อีกไม่เกิน "+0+" หน่วย การรับความเสียหายระหว่างผลพิเศษนี้จะไม่ทำให้พลังชีวิตต่ำกว่า 1 หน่วย");
                    sendSkillTriggerEvent(combatFlow, "Forbidden Rite triggered");
                } else {
                    double remain = xb - event.amount;
                    condition.putNumberRecord("DamageTakenRemain", remain);
                    condition.getCondition().setDescription("สูญเสียพลังชีวิตได้อีกไม่เกิน "+remain+" หน่วย การรับความเสียหายระหว่างผลพิเศษนี้จะไม่ทำให้พลังชีวิตต่ำกว่า 1 หน่วย");
                }

                if (event.amount >= current_health) {
                    event.amount = current_health - 1;
                    sendSkillTriggerEvent(combatFlow, "Forbidden Rite triggered [Undying]");
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
