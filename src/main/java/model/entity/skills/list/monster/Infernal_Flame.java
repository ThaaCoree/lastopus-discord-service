package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Infernal_Flame extends Skill implements SkillWithCondition {

    public static String NAME = "Infernal Flame";

    public Infernal_Flame() {
        super();
        setDescription("เคลือบอาวุธด้วยธาตุไฟจนกว่าจะจบการต่อสู้ เมื่อสร้างความเสียหายด้วยอาวุธนี้ มอบสถานะ Melting Heat ให้กับเป้าหมายเป็นเวลา XA รอบเทิร์น\n" +
                "Melting Heat : ลด VIT ลง XB");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(2);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.3*(1+DebuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XB").setPercent(true);
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
            int duration = (int) getSkillMultiplier().get("XA").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Melting Heat");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Melting Heat");
        condition.getStatusModifiers(StatusType.VITALITY).setGlobalMult(getSkillMultiplier().get("XB").getResult()*(-1));

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

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
