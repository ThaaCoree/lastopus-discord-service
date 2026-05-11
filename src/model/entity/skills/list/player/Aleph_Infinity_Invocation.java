package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.CounterName;
import model.type.SkillType;

public class Aleph_Infinity_Invocation extends Skill {

    public static String NAME = "ℵ∞ Invocation";

    public Aleph_Infinity_Invocation() {
        super();
        setDescription("The Iron Tomb ได้รับสกิลนี้เช่นกัน\n" +
                "ใช้ 3 วิวรณ์ เพื่อร่ายสกิลนี้, Invoke 3 ยูนิตที่เลือก 3 สแต็ค");
        setActionType("Action");
        setManaCost(12);
        setCooldown(2);
        getPureTags().add(SkillType.SPELL);
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
        getUser().counterSum(CounterName.PROVIDENCE, -3);
        if (!skillTarget.getTarget(0).isEmpty()) {
        int duration = 99;
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Invoked");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .condition(condition, duration)
                        .condition(condition, duration)
                        .addActType(ActType.CONDITION_GIVEN, ActType.CAST)
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
