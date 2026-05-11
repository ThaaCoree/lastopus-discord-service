package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.ResourceEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Extreme_Possibility extends Skill implements SkillWithCondition {

    public static String NAME = "Extreme Possibility";

    public Extreme_Possibility() {
        super();
        setDescription("มอบสถานะ Possibility Vortex ให้กับหนึ่งยูนิตพันธมิตร\n" +
                "ครั้งถัดไปที่มันสร้างความเสียหาย วิเนลพุ่งเข้าหาเป้าหมายจากตำแหน่งใดก็ได้ของสนาม สร้างความเสียหายกายภาพจำนวนเท่ากันด้วย");
        setActionType("Action");
        setManaCost(4);
        setCooldown(2);
        getPureTags().add(SkillType.PHYSICAL);
        getPureTags().add(SkillType.STRIKE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 0)
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
            Conditions condition = combatFlow.findCondition("Possibility Vortex");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Possibility Vortex");
        condition.setDescription("เมื่อสร้างความเสียหาย วิเนลจะสร้างความเสียหายกายภาพในจำนวนเท่ากัน");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (!event.source.hasCondition("Possibility Vortex")) return;
            if (!event.isDamage()) return;

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), event.target)
                            .effect(ActionEffectType.DAMAGE_PHYSICAL,event.amount, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE, ActType.SKILL_TRIGGER)
                            .build()
            );

            ConditionManager.removeCondition(event.source, "Possibility Vortex");
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
