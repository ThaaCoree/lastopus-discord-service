package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Starving_Beast extends Skill implements SkillWithCondition {

    public static String NAME = "Starving Beast";

    public Starving_Beast() {
        super();
        setDescription("เมื่อใช้งาน รับสถานะ Starving Beast เป็นเวลา XB รอบเทิร์น ซึ่งเมื่อสร้างความเสียหายในระหว่างนี้ ผู้ใช้จะฟื้นฟูพลังชีวิต XA หน่วย\n" +
                "หากมี Counter Claws อย่างน้อย 6 และ Fangs อย่างน้อย 3, จะฟื้นฟูพลังชีวิต XC หน่วยแทน");
        setActionType("Action");
        setManaCost(9);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.009*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.018*UsableHP"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
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
        if (!skillTarget.getTarget(0).isEmpty()) {

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Starving Beast");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Starving Beast");
        double xa = getSkillMultiplier().get("XA").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        condition.setDescription("เมื่อสร้างความเสียหาย ฟื้นฟูพลังชีวิต "+xa+" หน่วย, หากครบเงื่อนไข ฟื้นฟู "+xc+" หน่วยแทน");

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
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (event.unit_source != getUser()) return;
            for (Unit unit : event.unit_target) {
                if (event.canDamage(unit.getName()) && getUser().hasCondition("Starving Beast")) {
                    double claw = getUser().getCounter().get(CounterName.CLAWS);
                    double fang = getUser().getCounter().get(CounterName.FANGS);

                    if (claw >= 6 && fang >= 3) {
                        double xc = getSkillMultiplier().get("XC").getResult();
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), unit)
                                        .effect(ActionEffectType.HEALTH_RECOVER, xc, event.damage_times)
                                        .addActType(ActType.SKILL_TRIGGER, ActType.HEALTH_RECOVER)
                                        .build()
                        );
                    } else {
                        double xa = getSkillMultiplier().get("XB").getResult();
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), unit)
                                        .effect(ActionEffectType.HEALTH_RECOVER, xa, event.damage_times)
                                        .addActType(ActType.SKILL_TRIGGER, ActType.HEALTH_RECOVER)
                                        .build()
                        );
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
