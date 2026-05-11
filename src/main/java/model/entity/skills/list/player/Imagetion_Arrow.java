package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

import java.util.List;

public class Imagetion_Arrow extends Skill implements SkillWithCondition {

    public static String NAME = "Imagetion Arrow";

    public Imagetion_Arrow() {
        super();
        setDescription("เมื่อสร้างความเสียหายด้วยเวทมนตร์สำเร็จหรือถูกจู่โจม มีโอกาส XA ที่จะมอบสถานะ Forget ให้กับเป้าหมาย\n" +
                "Forget : ยกเลิก Action ครั้งถัดไป");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.35);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.35*(1+LUK/130)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.CHANCE);
        getSkillMultiplier().get("XA").setPercent(true);

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
        Conditions condition = new Conditions("Forget");
        condition.setDescription("ยกเลิก Action ครั้งถัดไป");

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (event.unit_source != getUser() || !event.hasActType(ActType.STRIKE) || !event.event_source.equals(getName())) return;
            List<Unit> targets = event.unit_target;

            int duration = 4;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Forget");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), targets)
                            .condition(condition, duration)
                            .addActType(ActType.SKILL_TRIGGER, ActType.CONDITION_GIVEN)
                            .build());
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
