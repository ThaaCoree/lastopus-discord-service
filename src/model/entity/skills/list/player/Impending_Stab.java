package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.ResourceEvent;
import main.controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Impending_Stab extends Skill implements SkillWithCondition {

    public static String NAME = "Impending Stab";

    public Impending_Stab() {
        super();
        setDescription("เมื่อสร้างความเสียหายให้กับยูนิตอื่น จะเกิดอาวุธเงาเล็งไปที่ยูนิตเป้าหมาย เมื่อจบรอบเทิร์น อาวุธเงาจะสร้างความเสียหายกายภาพ XA หน่วย\n" +
                "อาวุธเงาสามารถถูกทำลายได้ด้วย XB Combined Action\n" +
                "หากยูนิตเป้าหมายหมดสภาพต่อสู้ด้วยความเสียหายจากสกิลนี้ในระหว่างที่ในสนามมีหลักของ Shadow Field อย่างน้อย 3 ต้น Aard Archer จะใช้งาน Be One ได้ทันทีโดยไม่สูญเสีย MP และไม่สนใจคูลดาวน์");
        setActionType("Passive");
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SINGLE_TARGET);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
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
        Conditions condition = new Conditions("Impending Stab");
        double xa = getSkillMultiplier().get("XA").getResult();
        condition.setDescription("รับความเสียหายกายภาพ "+xa+" หน่วยเมื่อจบรอบเทิร์น");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(RoundEvent.class, EventPhase.POST, 0, (RoundEvent event) -> {
            combatFlow.getAllUnit().forEach((name, unit) -> {
                double xa = getSkillMultiplier().get("XA").getResult();
                for (ConditionInstance instance : unit.getConditionInstances().values()) {
                    if (instance.getCondition().getName().equals("Impending Stab")) {
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), unit)
                                        .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                        .addActType(ActType.STRIKE, ActType.SKILL_TRIGGER)
                                        .build()
                        );
                    }
                }
            });
        });

        eventBus.register(ActionEvent.class, EventPhase.POST, 0, event -> {
            if (event.damage_times <= 0) return;
            if (event.unit_source != getUser()) return;
            if (event.event_source.equals(getName())) return;

            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Impending Stab");
            for (int i = 0; i< event.damage_times; i++) {
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.unit_target)
                                .condition(condition, duration)
                                .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                .build());
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
