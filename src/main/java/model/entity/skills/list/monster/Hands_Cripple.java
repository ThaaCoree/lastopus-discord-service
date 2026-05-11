package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Hands_Cripple extends Skill implements SkillWithCondition {

    public static String NAME = "Hands Cripple";

    public Hands_Cripple() {
        super();
        setDescription("ใช้มนตร์แห่งความมืดหน่วงแขนของเป้าหมายไว้ ลด ATK XA เป็นเวลา XB รอบเทิร์น");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.3*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DARK);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Hands Cripple");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions condition = new Conditions("Hands Cripple");
        condition.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult()*(-1));
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult()*(-1));
        condition.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult()*(-1));

        condition.setConditionType(ConditionType.DEBUFF);
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
