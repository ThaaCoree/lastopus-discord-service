package model.entity.skills.list.item.offensive;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.EventPhase;
import model.type.SkillType;

public class Clock_Hand extends Skill {

    public static String NAME = "Clock Hand";

    public Clock_Hand() {
        super();
        setDescription("การโจมตีจะมอบสถานะ Time Warping ให้กับเป้าหมายเป็นเวลา XA รอบเทิร์น\n" +
                "Time Warping : มีโอกาส 35% ที่จะออก Action, Combined Action, Reaction พลาด");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEBUFF);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);
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
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.ATTACK) || event.unit_source != getUser()) return;

            int duration = (int) getSkillMultiplier().get("XA").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Time Warping");

            sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), event.unit_target)
                                        .condition(condition, duration)
                                        .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                        .build()
                        );
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
