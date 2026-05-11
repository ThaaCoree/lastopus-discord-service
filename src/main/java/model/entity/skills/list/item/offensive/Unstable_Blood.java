package model.entity.skills.list.item.offensive;

import controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.ConditionTierType;
import model.type.ConditionType;
import model.type.SkillType;

public class Unstable_Blood extends Skill implements SkillWithCondition {

    public static String NAME = "Unstable Blood";

    public Unstable_Blood() {
        super();
        setDescription("การโจมตีจะมอบ Unstable Blood ให้กับเป้าหมายเป็นเวลา XB รอบเทิร์น ซึ่งสร้างความเสียหายเวท XA ทุกครั้งที่จบเทิร์น");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*MATK*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("1"));
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
        Conditions condition = new Conditions("Unstable Blood");
        double xa = getSkillMultiplier().get("XA").getResult();

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);
        condition.setDescription("รับความเสียหาย "+xa+" หน่วยเมื่อจบรอบเทิร์น");

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
