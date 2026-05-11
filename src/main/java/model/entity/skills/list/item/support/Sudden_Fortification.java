package main.java.model.entity.skills.list.item.support;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.*;

public class Sudden_Fortification extends Skill {

    public static String NAME = "Sudden Fortification";

    public Sudden_Fortification() {
        super();
        setDescription("ใช้งานได้เมื่อมีการโจมตีเกิดขึ้น ทำให้เป้าหมายได้รับสถานะ Sudden Fortify ซึ่งทำให้ PDEF มีค่าเท่ากับผู้ใช้จนกว่าจะจบรอบเทิร์น");
        setActionType("Reaction");
        setManaCost(4);
        setCooldown(1);
        getPureTags().add(SkillType.DEFENSE);
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

            int duration = 1;

            Conditions condition = new Conditions("Sudden Fortify");
            double pdef = getUser().getStats().get(StatType.PHYSICALDEFENSE).getFinal();
            condition.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(pdef);

            condition.setConditionType(ConditionType.NEUTRAL);
            condition.setConditionTierType(ConditionTierType.ADVANCED);

            addConditionToDatabase(condition, combatFlow);

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build()
            );
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
