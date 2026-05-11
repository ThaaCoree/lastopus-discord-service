package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.RoundEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.PassiveNode;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Fatima extends Skill implements SkillWithCondition {

    public static String NAME = "Fatima";

    public Fatima() {
        super();
        setDescription("สร้างผ้าคลุมสีดำสามารถใช้บินหรือเคลื่อนที่ข้ามสิ่งกีดขวางได้ คงอยู่ XB รอบเทิร์น และในระหว่างที่ผลของสกิลยังอยู่ ฟื้นฟูพลังชีวิต XA หน่วย\n" +
                "เมื่อได้รับผลฮีลจาก Fatima ที่มากกว่าพลังชีวิต, เปลี่ยนการฟื้นฟูที่เกินทั้งหมดให้กลายเป็น Debris");
        setActionType("Action");
        setManaCost(7);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.35*MATK*(1+HealAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Fatima");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions condition = new Conditions("Fatima");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);
        condition.setDescription("บินได้ชั่วระยะเวลาหนึ่ง รับฮีล "+getSkillMultiplier().get("XA").getResultString()+" หน่วยทุกเทิร์น");

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        combatFlow.getEventBus().register(RoundEvent.class, EventPhase.POST, 0, event -> {
            combatFlow.getAllUnit().forEach((name, unit) -> {
                if (unit.hasCondition("Fatima")) {
                    double ia = 1;
                    double ua = 1;
                    for (PassiveNode node : getUser().getAllocatedPassives().values()) {
                        if (node.getName().equals("Intense Affection")) {
                            ia = 2;
                        }
                        if (node.getName().equals("Unaffected Affection")) {
                            ua = 0;
                        }
                    }
                    double toRecover = getSkillMultiplier().get("XA").getResult() * ia * ua;
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), getUser())
                                    .effect(ActionEffectType.HEALTH_RECOVER, toRecover, 1)
                                    .addActType(ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }
            });
        });

        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.MODIFY, -1, event -> {
            combatFlow.getAllUnit().forEach((name, unit) -> {
                if (unit.hasCondition("Fatima") && event.event_source.equals("Fatima")) {
                    double current_health = unit.getHealth().getRemaining();
                    double usable_health = unit.getHealth().getUsable();
                    double after_heal = current_health + event.getHeal(name);
                    if (after_heal > usable_health) {
                        event.doCreateDebris(after_heal - usable_health);
                        event.act_type.add(ActType.CREATE_DEBRIS);
                    }
                }
            });
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
