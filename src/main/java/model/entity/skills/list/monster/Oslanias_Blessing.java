package model.entity.skills.list.monster;

import controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Oslanias_Blessing extends Skill implements SkillWithCondition {

    public static String NAME = "Oslania's Blessing";

    public Oslanias_Blessing() {
        super();
        setDescription("อวยพรให้กับสมาชิกขบวนจันทราของตน Moon Servant ทั้งหมดในสนามได้รับสถานะ Oslania's Blessing ซึ่งมอบ\n" +
                "STR, AGI, DEX XA หน่วย\n" +
                "VIT, INT, WIS XB หน่วย\n" +
                "MaxHP XC หน่วย, PATK และ RATK XD หน่วย");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.8*DEX*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.6*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.3*HP*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.4*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.BUFF);
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
        for (Unit unit : combatFlow.getAllUnit().values()) {
            if (!unit.getName().contains("Moon Servant")) continue;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Oslania's Blessing");
            ConditionManager.applyCondition(condition, unit, 99);
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Oslania's Blessing");
        condition.getStatusModifiers(StatusType.STRENGTH).setFlat(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.AGILITY).setFlat(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.DEXTERITY).setFlat(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.VITALITY).setFlat(getSkillMultiplier().get("XB").getResult());
        condition.getStatusModifiers(StatusType.INTELLIGENCE).setFlat(getSkillMultiplier().get("XB").getResult());
        condition.getStatusModifiers(StatusType.WISDOM).setFlat(getSkillMultiplier().get("XB").getResult());
        condition.getStatModifiers(StatType.HEALTHPOINT).setFlat(getSkillMultiplier().get("XC").getResult());
        condition.getStatModifiers(StatType.PHYSICALATTACK).setFlat(getSkillMultiplier().get("XD").getResult());
        condition.getStatModifiers(StatType.RANGEDATTACK).setFlat(getSkillMultiplier().get("XD").getResult());

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
