package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Moondance extends Skill implements SkillWithCondition {

    public static String NAME = "Moondance";

    public Moondance() {
        super();
        setDescription("มอบสถานะ Moondance ให้กับตัวเองซึ่งมอบ ATK XA\n" +
                "Moondance สามารถทับซ้อนได้ไม่จำกัด มีระยะเวลา XB รอบเทิร์น เมื่อทับซ้อนจะรีเซ็ตระยะเวลา");
        setActionType("Action");
        setManaCost(0);
        setCooldown(1);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.05*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
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
//        , 0, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(CounterName.MOONDANCE)) {
            getUser().getRawCounterMap().put(CounterName.MOONDANCE, 0.0);
            getUser().getCounter().put(CounterName.MOONDANCE, 0.0);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Moondance");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());

        for (ConditionInstance instance : getUser().getConditionInstances().values()) {
            if (instance.getCondition().getName().equals("Moondance")) {
                instance.setDurationRemain(duration);
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Moondance");
        double danceCounter = getUser().getRawCounterMap().get(CounterName.MOONDANCE);
        condition.getStatModifiers(StatType.PHYSICALPENETRATE).setGlobalMult(getSkillMultiplier().get("XA").getResult()*danceCounter);

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
