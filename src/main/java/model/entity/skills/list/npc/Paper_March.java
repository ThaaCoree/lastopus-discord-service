package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Paper_March extends Skill implements SkillWithCondition {

    public static String NAME = "Paper March";

    public Paper_March() {
        super();
        setDescription("สลายกระดาษ XA แผ่น มอบสถานะ Paper Marching ซึ่งมอบ Evasion XB, Movement Speed XC และการเคลื่อนไหวแบบลอยได้ให้กับเป้าหมายเป็นเวลา XD รอบเทิร์น");
        setActionType("Combine");
        setManaCost(10);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("4"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.3*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.4*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DURATION);
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
        double paper_count = getSkillMultiplier().get("XA").getResult();
        getUser().counterSum(CounterName.PAPER, paper_count*-1);
        if (!skillTarget.getTarget(0).isEmpty()) {
            int duration = (int) getSkillMultiplier().get("XD").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Paper Marching");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Paper Marching");
        condition.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XC").getResult());

        condition.setConditionType(ConditionType.BUFF);
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
