package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillWithCondition;
import model.entity.units.Unit;
import model.type.*;

public class Omega_Function_Invocation extends Skill implements SkillWithCondition {

    public static String NAME = "Ω(x) Invocation";

    public Omega_Function_Invocation() {
        super();
        setDescription("Passive : เมื่อจบรอบเทิร์น Tezzeract จะส่งเสียงคำราม ปลุกยูนิตพันธมิตรทั้งหมด\n" +
                "\n" +
                "The Iron Tomb ได้รับสกิลนี้เช่นกัน, ใช้ 5 วิวรณ์ เพื่อร่ายสกิลนี้, Invoke ยูนิตพันธมิตร 1 ยูนิต 1 Stack,\n" +
                "หากร่ายสกิลนี้ก่อน Tezzeract จะส่งเสียงคำราม ทำการ Dark Heal 6.66 หน่วย ให้กับยูนิตพันธมิตรทั้งหมดที่ Invoked และมี HP เป็น 0 จากนั้น นำ Invoked ออก 1 Stack จากยูนิตนั้น");
        setActionType("Action");
        setManaCost(9);
        setCooldown(2);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.RECOVERY);
        getPureTags().add(SkillType.HEALING);
        setManaReservePercent(0.25);
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
        getUser().counterSum(CounterName.PROVIDENCE, -5);

        if (!skillTarget.getTarget(0).isEmpty()) {
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Invoked");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN, ActType.CAST)
                            .build()
            );
        }
        int duration = 1;
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Ω(x) Invocation");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition, duration)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Ω(x) Invocation");
        condition.setDescription("เมื่อจบรอบเทิร์นก่อนที่ Tezzeract จะส่งเสียง, Dark Heal ให้ยูนิตพันธมิตรทั้งหมดที่ถูก Invoke 6.66 หน่วย และลดจำนวนการ Invoke ลง 1 สแต็ค");

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
        eventBus.register(RoundEvent.class, EventPhase.MODIFY, 0, (RoundEvent event) -> {
            if (!getUser().hasCondition("Ω(x) Invocation")) return;
            combatFlow.getAllUnit().forEach((s, unit) -> {
                if (!unit.hasCondition("Invoked")) return;
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), unit)
                                .effect(ActionEffectType.HEALTH_RECOVER, 6.66, 1)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                .build()
                );
                ConditionManager.removeOneCondition(unit, "Invoked");
            });
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
