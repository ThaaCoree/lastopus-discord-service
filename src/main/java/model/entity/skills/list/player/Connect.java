package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Connect extends Skill implements SkillWithCondition {

    public static String NAME = "Connect";

    public Connect() {
        super();
        setDescription("เลือกบัพให้สองเป้าหมายเป็นระยะเวลา XB รอบเทิร์น เมื่อผู้ถือครองบัพจู่โจม จะจู่โจมออกไปพร้อมกันด้วย การจู่โจมด้วยผลของสกิลนี้สามารถส่งผลได้สูงสุดสามครั้งต่อเทิร์นของผู้ได้รับบัพและความเสียหายที่ทำได้จะลดลง XA\n" +
                "เมื่อ Shiranui ฮีล, ยูนิตที่ได้รับผลของสกิลนี้และตัว Shiranui จะได้รับฮีล XC ของจำนวนนั้นด้วย");
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

        addMultiplier("XC", "0.5");
        addTag("XC", SkillType.SCALING);
        addTag("XC", SkillType.LIMIT);
        addTag("XC", SkillType.RECOVERY);
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
            }
        });

        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.POST, 0, event -> {
            if (!event.hasActType(ActType.HEAL)) return;
            if (event.unit_source != getUser()) return;
            if (event.event_source.equals(this.getName())) return;

            for (Unit unit : event.unit_target) {
                int heal_times = event.heal_times;
                if (unit.hasCondition("Connect")) {
                    double heal = event.getHeal(unit.getName());
                    heal *= getMultiplierResult("XC");
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), event.unit_target)
                                    .effect(ActionEffectType.HEALTH_RECOVER, heal, heal_times)
                                    .addActType(ActType.SKILL_TRIGGER, ActType.HEAL, ActType.HEALTH_RECOVER)
                                    .build()
                    );
                }

            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
