package model.entity.skills.list.player;

import controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.type.*;

public class Destiny_Born extends Skill {

    public static String NAME = "Destiny Born";

    public Destiny_Born() {
        super();
        setDescription("ได้รับสถานะ Destiny Born อยู่ตลอดเวลา ซึ่งเพิ่ม CritChance XA");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.35);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.01*(1+LUK*0.25)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);
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
        if (getUser().hasCondition("Destiny Born") && !getIsActive()) {
            ConditionManager.removeCondition(getUser(), "Destiny Born");
        }
        if (!getUser().hasCondition("Destiny Born") && getIsActive()) {
        Conditions condition = new Conditions("Destiny Born");
        condition.getStatModifiers(StatType.CRITCHANCE).setGlobalMult(getSkillMultiplier().get("XA").getResult());

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        ConditionManager.applyCondition(condition,getUser(), getUser(), 99);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
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
