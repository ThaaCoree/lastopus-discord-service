package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Undone extends Skill implements SkillWithCondition {

    public static String NAME = "Undone";

    public Undone() {
        super();
        setDescription("เมื่อสร้างความเสียหายมากกว่าหนึ่งครั้งในการโจมตีชุดเดียว ได้รับผลพิเศษดังต่อไปนี้ ขึ้นอยู่กับว่าสร้างความเสียหายกี่ครั้งในการโจมตีนั้น\n" +
                "3 ครั้ง : ได้รับสถานะ Wings เป็นเวลา XA รอบเทิร์น\n" +
                "5 ครั้ง : ความเสียหายทั้งหมดในการโจมตีนี้ไม่สนใจ PDEF และ MDEF\n" +
                "8 ครั้ง : มอบสถานะ Tremble ให้กับเป้าหมายเป็นเวลา XB รอบเทิร์น (ทับซ้อนไม่ได้)\n" +
                "15 ครั้ง : ได้รับ 1 Combined Action กลับคืน\n" +
                "20 ครั้ง : ความเสียหายทั้งหมดจะถูกผนวกรวมเป็นครั้งเดียว ผู้ใช้สามารถเล็งจุดที่จู่โจมได้อย่างแม่นยำ\n" +
                "Wings : ได้รับ MSPD และ Evasion XC");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.STRIKE);
        setManaReservePercent(0.5);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("1"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.05"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser());
//                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
//        );
//        spec    .addFields(
//                new SkillInputSpec.InputField<String>("Hits", SkillInputSpec.InputType.NUMBER, 0)
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

    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Wings");
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XC").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

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
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
            if (event.unit_source != getUser()) return;
            if (event.damage_times < 3) return;

            for (Unit target : event.unit_target) {
                String name = target.getName();
                if (event.damage_times >= 3) {

                    int wings_duration = (int) getSkillMultiplier().get("XA").getResult();
                    Conditions wings = combatFlow.getDatabase().getAllConditionMap().get("Wings");
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), getUser())
                                    .condition(wings, wings_duration)
                                    .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }

                if (event.damage_times >= 5) {
                    event.ignore_def = true;
                }

                if (event.damage_times >= 7) {
                    int tremble_duration = (int) getSkillMultiplier().get("XB").getResult();
                    if (!target.hasCondition("Tremble")) {
                        Conditions tremble = combatFlow.getDatabase().getAllConditionMap().get("Tremble");
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), target)
                                        .condition(tremble, tremble_duration)
                                        .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                        .build()
                        );
                    }
                }

                if (event.damage_times >= 15) {
                    event.addAllDamageMultModifier(event.damage_times-1);
                    event.damage_times = 1;
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
