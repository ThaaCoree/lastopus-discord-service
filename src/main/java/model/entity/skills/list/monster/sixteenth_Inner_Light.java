package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.type.*;

public class sixteenth_Inner_Light extends Skill implements SkillWithCondition {

    public static String NAME = "The 16th Blessing, Inner Light";

    public sixteenth_Inner_Light() {
        super();
        setDescription("ร่ายรำ มอบสถานะ Inner Light ให้พันธมิตรทั้งหมดจนกว่าจะสิ้นสุดการต่อสู้หรือหยุดร่ายรำ\n" +
                "Inner Light : เพิ่มสเตตัสทั้งหมด XA");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.CEREMONY);
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        int duration = 99;

        Conditions condition = new Conditions("Inner Light");
        condition.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.VITALITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.DEXTERITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.INTELLIGENCE).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.WISDOM).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.LUCK).setGlobalMult(getSkillMultiplier().get("XA").getResult());

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getAllies(combatFlow))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {

    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
