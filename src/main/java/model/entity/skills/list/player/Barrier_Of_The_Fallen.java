package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Barrier_Of_The_Fallen extends Skill implements SkillWithCondition {

    public static String NAME = "Barrier of the Fallen Paradise";

    public Barrier_Of_The_Fallen() {
        super();
        setDescription("The Iron Tomb ได้รับสกิลนี้เช่นกัน, ใช้ 6 วิวรณ์ หรือ 1 Love Train เพื่อร่ายสกิลนี้,\n" +
                "เมื่อร่ายสกิลนี้โดยใช้ Love Train สามารถร่ายได้โดยไม่สนใจคูลดาวน์ และไม่ใช้มานา,\n" +
                "บล็อกการจู่โจมที่เข้ามา และเลือกแสดงผลเพิ่มเติม 1 อย่างจากรายการต่อไปนี้,\n" +
                "Solarflare Permanent - บล็อกการจู่โจมทั้งหมดที่อยู่ในเหตุการณ์เดียวกัน\n" +
                "Nebula Erudition - หลังจากการจู่โจมนั้น เลือก 1 ยูนิต วิเคราะห์มัน 1 ครั้ง\n" +
                "Vortex Elation - Akivili ได้รับ MATK XC เป็นเวลา XA รอบเทิร์น และการจู่โจมของ Akivili จะสร้าง True Damage 0 หน่วย จนกว่าจะจบการต่อสู้\n" +
                "Stardust Remembrance - วาร์ป The Iron Tomb ไปยังตำแหน่งของ Akivili ก่อนเกิดการจู่โจม\n" +
                "\n" +
                "Divine Intervention: ได้รับ 1 Love Train\n" +
                "\n" +
                "Divine Invocation: ได้รับ XB Love Train");
        setActionType("Reaction");
        setManaCost(14);
        setCooldown(1);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.05*(HP-UsableHP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SCALING);
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
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(CounterName.LOVE_TRAIN)) {
            getUser().getRawCounterMap().put(CounterName.LOVE_TRAIN,0.0);
            getUser().getCounter().put(CounterName.LOVE_TRAIN,0.0);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.findCondition("Vortex Elation");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Vortex Elation");
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XC").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
//        EventBus eventBus = combatFlow.getEventBus();
//        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
//            if (!event.hasActType(ActType.HEAL) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
//            List<Unit> targets = event.unit_target;
//            double heal_amount = event.getHeal();
//
//            sendActionEvent(combatFlow.getEventBus(),
//                    ActionEvent.builder(getName(), getUser(), targets)
//                            .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                            .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                            .build()
//            );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
