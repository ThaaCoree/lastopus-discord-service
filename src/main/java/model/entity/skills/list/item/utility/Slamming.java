package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;

public class Slamming extends Skill {

    public static String NAME = "Slamming";

    public Slamming() {
        super();
        setDescription("เมื่อสร้างความเสียหายสำเร็จ มอบสถานะ Tremble ให้กับเป้าหมายเป็นเวลา XA เทิร์น");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
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
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (event.source != getUser()) return;
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {

                int duration = (int) getSkillMultiplier().get("XA").getResult();
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Tremble");

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.target)
                                .condition(condition, duration)
                                .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                .build()
                );
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
