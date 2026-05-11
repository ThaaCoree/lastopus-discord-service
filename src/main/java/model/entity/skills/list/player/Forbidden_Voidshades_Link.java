package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.ResourceEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;
import util.LogWriterUtil;

public class Forbidden_Voidshades_Link extends Skill implements SkillWithCondition {

    public static String NAME = "Forbidden Voidshades Link";

    public Forbidden_Voidshades_Link() {
        super();
        setDescription("เลือกยูนิตพันธมิตร ทำพิธีเชื่อมต่อกับหลักเงา\n" +
                "เมื่อยูนิตที่ถูกเชื่อมต่อจะได้รับความเสียหายที่ทำให้หมดสภาพการต่อสู้ ลบล้างความเสียหายนั้นทั้งหมดแล้วทำลายเสาที่ถูกเชื่อมต่ออยู่");
        setActionType("Combine");
        setManaCost(6);
        setCooldown(1);
        setHealthReservePercent(0.20);
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Voidshades Linked");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Voidshades Linked");
        condition.setDescription("กำลังเชื่อมต่อกับหลักเงา เมื่อได้รับความเสียหายจนทำให้หมดสภาพต่อสู้ ยกเลิกความเสียหายนั้นและทำลายหลักเงาแทน");

        condition.setConditionType(ConditionType.BUFF);
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
        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, 0, (ResourceEvent event) -> {
            if (!event.target.hasCondition("Voidshades Linked")) return;
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                double current_health = getUser().getHealth().getRemaining();
                if (event.amount >= current_health) {
                    LogWriterUtil.log(">Voidshades Link triggered");
                    event.amount = 0;
                    ConditionManager.removeCondition(event.target, "Voidshades Linked");
                }

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.target)
                                .addActType(ActType.SKILL_TRIGGER)
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
