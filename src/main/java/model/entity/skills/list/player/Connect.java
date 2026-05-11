package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Connect extends Skill implements SkillWithCondition {

    public static String NAME = "Connect";

    public Connect() {
        super();
        setDescription("เลือกบัพให้สองเป้าหมายเป็นระยะเวลา XB รอบเทิร์น เมื่อผู้ถือครองบัพจู่โจม จะจู่โจมออกไปพร้อมกันด้วย การจู่โจมด้วยผลของสกิลนี้สามารถส่งผลได้สูงสุดสามครั้งต่อเทิร์นของผู้ได้รับบัพและความเสียหายที่ทำได้จะลดลง XA\n" +
                "เมื่อจู่โจมด้วยผลพิเศษนี้ครบ 5 ครั้ง คูลดาวน์สกิลทั้งหมดของผู้ถือครองบัพจะลดลงหนึ่งรอบเทิร์น");
        setActionType("Action");
        setManaCost(9);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Connect");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions condition = new Conditions("Connect");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);
        double atk = getUser().getStats().get(StatType.MAGICALATTACK).getFinal();
        double xa = getSkillMultiplier().get("XA").getResult();
        condition.setDescription("เมื่อจู่โจม, Shiranui จะจู่โจมออกไปพร้อมกันด้วย สร้างความเสียหายเวท "+atk*(1-xa)+" หน่วย");

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.POST, 0, event -> {
            if (!event.hasActType(ActType.STRIKE)) return;
            if (event.unit_source.hasCondition("Connect") && event.unit_source != getUser()) {
                double atk = getUser().getStats().get(StatType.MAGICALATTACK).getFinal();
                double xa = getSkillMultiplier().get("XA").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.unit_target)
                                .effect(ActionEffectType.DAMAGE_MAGICAL, atk*(1-xa), 1)
                                .addActType(ActType.CAST, ActType.STRIKE)
                                .build()
                );
                getUser().counterIncrement(CounterName.CONNECT_STRIKE);
                if (getUser().getCounter().get(CounterName.CONNECT_STRIKE) >= 5) {
                    for (Unit unit : combatFlow.getAllUnit().values()) {
                        if (unit.hasCondition("Connect")) {
                            for (SkillInstance instance : unit.getAllSkill().values()) {
                                instance.cooldownDecrement();
                            }
                        }
                    }
                    getUser().counterSet(CounterName.CONNECT_STRIKE, 0);
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
