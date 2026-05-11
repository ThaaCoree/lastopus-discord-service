package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.ConditionTierType;
import model.type.ConditionType;
import model.type.SkillType;
import model.type.StatType;

public class Verifyr_Passion extends Skill implements SkillWithCondition {

    public static String NAME = "Verifyr's Passion";

    public Verifyr_Passion() {
        super();
        setDescription("เมื่อได้รับสถานะ Sleep จะได้รับสถานะ Sleepwalking ด้วย ซึ่งทำให้ยังเคลื่อนไหว, ควบคุมร่างกายและคิดได้อย่างปกติ ระหว่างนี้ เพิ่ม ATK XA และ HealAMP XB");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").setPercent(true);
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
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Sleepwalking");
        condition.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatModifiers(StatType.HEALAMPLIFIER).setGlobalMult(getSkillMultiplier().get("XB").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        if (getUser().hasCondition("Sleep")) {
            ConditionManager.removeCondition(getUser(), "Sleepwalking");
            double sleep_dura = getUser().findCondition("Sleep").getDurationRemain();
            ConditionManager.applyCondition(condition, getUser(), (int) sleep_dura);
        }

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
