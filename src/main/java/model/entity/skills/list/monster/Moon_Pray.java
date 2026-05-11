package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Moon_Pray extends Skill implements SkillWithCondition {

    public static String NAME = "Moon Pray";

    public Moon_Pray() {
        super();
        setDescription("อ้อนวอนต่อจันทรา มอบสถานะ Moon Bless ให้กับตนเองจนกว่าจะจบการต่อสู้\n" +
                "Moon Bless : สเตตัสทั้งหมดเพิ่มขึ้น XA");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
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
        if (!skillTarget.getTarget(0).isEmpty()) {

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = 99;
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Moon Bless");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Moon Bless");
        condition.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.WISDOM).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.INTELLIGENCE).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.LUCK).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.DEXTERITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.VITALITY).setGlobalMult(getSkillMultiplier().get("XA").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
