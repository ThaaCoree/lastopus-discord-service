package model.entity.skills.list.item.offensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.ResourceEvent;
import main.controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillWithCondition;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class Deaths_Chosen extends Skill implements SkillWithCondition {

    public static String NAME = "Death's Chosen";

    public Deaths_Chosen() {
        super();
        setDescription("เมื่อเริ่มการต่อสู้ เลือกศัตรูหนึ่งยูนิต\n" +
                "เมื่อยูนิตนั้นรับความเสียหายตั้งแต่ครั้งที่สองเป็นต้นไป ดึงเอาความเสียหายที่มันเคยได้รับก่อนหน้านี้ทั้งหมด 15% มาสร้างความเสียหายซ้ำด้วย\n" +
                "ผู้ใช้รับผลแบบเดียวกัน รีเซ็ตเมื่อยูนิตที่เลือกหมดสภาพต่อสู้หรือตาย\n" +
                "เมื่อรีเซ็ต เลือกยูนิตศัตรูใหม่\n" +
                "\n" +
                "ผลพิเศษนี้คงอยู่แม้ว่าจะสูญเสียสกิล Death's Chosen");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DRAWBACK);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
//        spec    .addFields(
//                new SkillInputSpec.InputField<String>("Restart Fight", SkillInputSpec.InputType.BOOLEAN, 0)
//        , 0, 0);
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
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Death's Chosen");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build());

            if (!getUser().hasCondition("Death's Chosen")) {
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getUser())
                                .condition(condition, duration)
                                .addActType(ActType.CONDITION_GIVEN)
                                .build());
            }
            ConditionManager.removeCondition(getUser(), "Death's Calling");
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Death's Chosen");
        condition.setDescription("กำลังเป็นที่รักของความตาย ยูนิตนี้จะจดจำความเสียหาย 15% ที่ได้รับ และรับความเสียหายนั้นซ้ำเมื่อได้รับความเสียหาย");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (!event.target.hasCondition("Death's Chosen")) return;
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL)) {
                double damage = event.amount * 0.15;

                Conditions condition = new Conditions("Death's Calling");
                condition.setDescription("เมื่อได้รับความเสียหาย จะได้รับความเสียหายเพิ่มเติม " + (int) damage + " หน่วย");

                condition.setConditionType(ConditionType.NEUTRAL);
                condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.target)
                                .condition(condition, 99, "AdditionalDamage", damage)
                                .addActType(ActType.SKILL_TRIGGER, ActType.CONDITION_GIVEN)
                                .build()
                );
            }
        });

        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 1, (ActionEvent event) -> {
            if (event.event_source.equals(getName())) return;
            for (Unit target : event.unit_target) {
                if (target.hasCondition("Death's Calling") && event.canDamage(target.getName())) {
                    double sum_damage = 0;
                    for (ConditionInstance instance : target.getConditionInstances().values()) {
                        if (!instance.getCondition().getName().equals("Death's Calling")) continue;
                        sum_damage += instance.getNumberRecordOrDefault("AdditionalDamage", 0);
                    }

                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), event.unit_target)
                                    .effect(ActionEffectType.DAMAGE_PURE, sum_damage, 1)
                                    .addActType(ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }
            }
        });

        eventBus.register(RoundEvent.class, EventPhase.POST, 0, (RoundEvent event) -> {
            for (Unit target : combatFlow.getAllUnit().values()) {
                if (target.hasCondition("Death's Calling")) {
                    double sum_damage = 0;
                    for (ConditionInstance instance : target.getConditionInstances().values()) {
                        if (!instance.getCondition().getName().equals("Death's Calling")) continue;
                        sum_damage += instance.getNumberRecordOrDefault("AdditionalDamage", 0);
                    }
                    Map<String, Double> number_record = new LinkedHashMap<>();

                    Conditions condition = new Conditions("Death's Calling");
                    condition.setDescription("เมื่อได้รับความเสียหาย จะได้รับความเสียหายเพิ่มเติม " + (int) sum_damage + " หน่วย");

                    condition.setConditionType(ConditionType.NEUTRAL);
                    condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

                    number_record.put("AdditionalDamage", sum_damage);

                    ConditionManager.removeCondition(target, "Death's Calling");
                    ConditionManager.applyCondition(condition, target, target, 99, number_record);
                }

            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
