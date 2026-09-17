package model.entity.skills.list.summon;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Stars_Guidance extends Skill implements SkillWithCondition {

    public static String NAME = "Star's Guidance";

    public Stars_Guidance() {
        super();
        setDescription("สร้างประกายแสงขึ้นห้อมล้อมพันธมิตรทั้งหมด มอบสถานะ Star Infusion ให้เป็นเวลา XB รอบเทิร์น\n" +
                "Star Infusion : เพิ่มสเตตัสทั้งหมด XA");
        setActionType("Action");
        setManaCost(15);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.25*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.findCondition("Star Infusion");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getAllies(combatFlow))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Star Infusion");
        condition.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.DEXTERITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.VITALITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.WISDOM).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.INTELLIGENCE).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.LUCK).setGlobalMult(getSkillMultiplier().get("XA").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);

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
