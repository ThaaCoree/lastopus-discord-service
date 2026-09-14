package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.*;

public class Purify extends Skill {

    public static String NAME = "Purify";

    public Purify() {
        super();
        setDescription("ลบล้างดีบัพทั้งหมดที่ส่งผลกับพันธมิตรเป้าหมาย XA ยูนิต");
        setActionType("Turn");
        setManaCost(8);
        setCooldown(2);
        getPureTags().add(SkillType.DISPEL);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            for (String name : skillTarget.getTarget(0)) {
                Unit unit = combatFlow.findUnit(name);
                if (unit != null) {
                    unit.getConditionInstances().entrySet().removeIf(entry -> {
                        ConditionInstance conditionInstance = entry.getValue();

                        return conditionInstance.getCondition().getConditionType()
                                .equals(ConditionType.DEBUFF)
                                && !conditionInstance.getCondition().getConditionTierType()
                                .equals(ConditionTierType.BOUND)
                                && !conditionInstance.getCondition().getConditionTierType()
                                .equals(ConditionTierType.UNDISPELLABLE);
                    });
                }
            }
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
