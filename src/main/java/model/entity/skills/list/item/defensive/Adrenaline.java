package main.java.model.entity.skills.list.item.defensive;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.ActType;
import main.java.model.type.SkillType;

public class Adrenaline extends Skill {

    public static String NAME = "Adrenaline";

    public Adrenaline() {
        super();
        setDescription("เมื่อได้รับความเสียหายจากการโจมตีผนวกความเร็ว รับความเสียหายน้อยลงครึ่งหนึ่งจนกว่าจะหมดรอบเทิร์นนี้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
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

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = 1;
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Adrenaline");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                        .build());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
