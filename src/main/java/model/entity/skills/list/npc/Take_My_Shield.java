package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillWithCondition;
import model.entity.units.Unit;
import model.type.*;

public class Take_My_Shield extends Skill {

    public static String NAME = "TAKE MY SHIELD!";

    public Take_My_Shield() {
        super();
        setDescription("มอบ Block ทั้งหมดของตนเองให้พันธมิตรอื่นทั้งหมดเป็นเวลา XA รอบเทิร์น\n" +
                "ระหว่างนี้ Block ของผู้ใช้กลายเป็น 0");
        setActionType("Action");
        setManaCost(6);
        setCooldown(3);

        addTag(SkillType.PHYSICAL);

        addMultiplier("XA", "2");
        addTag("XA", SkillType.DURATION);

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
        int duration = (int) getSkillMultiplier().get("XA").getResult();
        double pblock = getUser().getStats().get(StatType.PHYSICALBLOCK).getFinal();
        double mblock = getUser().getStats().get(StatType.MAGICALBLOCK).getFinal();
        Conditions condition = new Conditions("TAKE MY SHIELD!");
        condition.getStatModifiers(StatType.MAGICALBLOCK).setFlat(mblock);
        condition.getStatModifiers(StatType.PHYSICALBLOCK).setFlat(pblock);

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);


        Conditions condition2 = new Conditions("GIVEN SHIELD!");
        condition2.getStatModifiers(StatType.MAGICALBLOCK).setOverride(0);
        condition2.getStatModifiers(StatType.PHYSICALBLOCK).setOverride(0);

        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.BOUND);

        addConditionToDatabase(condition, combatFlow);

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getOtherAllies(combatFlow))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition2, duration)
                        .addActType(ActType.CONDITION_GIVEN)
                        .build());
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
