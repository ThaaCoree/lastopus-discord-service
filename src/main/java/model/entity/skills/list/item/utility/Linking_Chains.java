package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillWithCondition;
import main.java.model.entity.units.Unit;
import main.java.model.type.ConditionTierType;
import main.java.model.type.ConditionType;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

public class Linking_Chains extends Skill implements SkillWithCondition {

    public static String NAME = "Linking Chains";

    public Linking_Chains() {
        super();
        setDescription("เลือกเป้าหมายหนึ่งยูนิต มอบสถานะ Linked Chains ให้ ซึ่งทำให้ Evasion ของเป้าหมายมีค่าเท่ากับตนเอง\n" +
                "เป้าหมายจะรับความเสียหายที่ผู้ใช้หลบพลาด และผู้ใช้จะรับความเสียหายที่เป้าหมายหลบพลาด");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.SPELL);
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
        Conditions condition = new Conditions("Linked Chains");
        double evasion = getUser().getStats().get(StatType.EVASION).getFinal();
        condition.getStatModifiers(StatType.EVASION).setOverride(evasion);

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.ADVANCED);
        condition.setDescription("จะรับความเสียหายแทนเมื่อคู่พันธะหลบพลาด คู่พันธะจะรับความเสียหายแทนเมื่อยูนิตนี้หลบพลาด");

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
