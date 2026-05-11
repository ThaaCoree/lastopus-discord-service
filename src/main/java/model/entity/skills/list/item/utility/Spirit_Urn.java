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

public class Spirit_Urn extends Skill implements SkillWithCondition {

    public static String NAME = "Spirit Urn";

    public Spirit_Urn() {
        super();
        setDescription("วางไหจิตวิญญาณลง ณ พื้นที่หนึ่ง ลด MSPD, Evasion, AttackSPD ของยูนิตศัตรูทั้งหมดในรัศมี XC เมตรลง XA ผลนี้คงอยู่นานอีก XB รอบเทิร์นแม้เป้าหมายจะเคลื่อนที่ออกจากพื้นที่ของไหจิตวิญญาณแล้ว");
        setActionType("Turn");
        setManaCost(20);
        setCooldown(0);
        getPureTags().add(SkillType.SPELL);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("3"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.AOE);
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
        Conditions condition = new Conditions("Spirit Drained");
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XA").getResult() * (-1));
        condition.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XA").getResult() * (-1));
        condition.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(getSkillMultiplier().get("XA").getResult() * (-1));

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

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
