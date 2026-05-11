package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Kyrraneth extends Skill implements SkillWithCondition {

    public static String NAME = "Kyr'raneth";

    public Kyrraneth() {
        super();
        setDescription("จะใช้งานได้เมื่อมียูนิตที่มีสถานะ Ka´rahn อยู่ในสนามเท่านั้น\n" +
                "เพิ่มขอบเขตการรับรู้ของดวงตา เพิ่ม CritChance XA หากมีบัพ Vah'ris อยู่ เพิ่ม CritChance อีก XB");
        setActionType("Combine");
        setManaCost(7);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.24*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("1*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
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
        int duration = (int) getSkillMultiplier().get("XC").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Kyr'raneth");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions condition = new Conditions("Kyr'raneth");
        double toIncrease = getSkillMultiplier().get("XA").getResult();
        double withValnis = 0;
        if (getUser().hasCondition("Val'nis"))
            withValnis = getSkillMultiplier().get("XC").getResult();
        toIncrease += withValnis;
        condition.getStatModifiers(StatType.CRITCHANCE).setFlat(toIncrease);

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

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
