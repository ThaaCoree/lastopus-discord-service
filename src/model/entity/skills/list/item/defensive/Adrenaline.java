package model.entity.skills.list.item.defensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.EventPhase;
import model.type.SkillType;

import java.util.List;

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
