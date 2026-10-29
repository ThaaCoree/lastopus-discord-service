package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Varkha_Tel extends Skill {

    public static String NAME = "Varkha'Tel";

    public Varkha_Tel() {
        super();
        setDescription("เมื่อเริ่มการต่อสู้ เลือกหนึ่งเป้าหมายให้อยู่ในสถานะ RaukThar หลังจากมีการจู่โจมใส่เป้าหมายที่ติดสถานะนี้ Onebrek\n" +
                "จะสามารถออกการจู่โจมตามไปได้หนึ่งครั้ง ในรอบเทิร์นนี้สามารถจู่โจมด้วยวิธีนี้ได้ XA ครั้ง  หากจู่โจมออกไปด้วยวิธีนี้จะได้รับชาร์จ Stimulate 1 ชาร์จ\n" +
                "เมื่อ Stimulate ครบ XC ชาร์จ Onebrek จะจู่โจมออกไปอีกหนึ่งครั้ง สร้างความเสียหายมากขึ้น XD และใช้ Stimulate XC ชาร์จเพื่อเพิ่มความเสียหายแบบเดียวกันให้กับการจู่โจมหลังจากนี้ XB ครั้ง \n" +
                "เมื่อเป้าหมายที่มี RaukThar หมดสภาพต่อสู้ สามารถเลือกสถานะย้ายไปยังยูนิตอื่นได้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.75);

        addMultiplier("XA", "3");
        addTag("XA", SkillType.LIMIT);
        addTag("XA", SkillType.STRIKE);

        addMultiplier("XB", "3");
        addTag("XB", SkillType.LIMIT);

        addMultiplier("XC", "3");
        addTag("XC", SkillType.REQUIREMENT);

        addMultiplier("XD", "0.5");
        addTag("XD", SkillType.SCALING);
        setPercent("XD");
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
//        , 0, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        addCounter(CounterName.VARKHA_TEL);
        addCounter(CounterName.VARKHA_TEL_STIMULATE);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.removeCondition(unit, "RaukThar");
        }
        if (!skillTarget.getTarget(0).isEmpty()) {
            Conditions condition = new Conditions("RaukThar");
            condition.setDescription("เมื่อถูกจู่โจม, Onebrek จะจู่โจมเป้าหมายนี้ด้วย");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, 99)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build()
            );
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE)) return;
            if (!isActive()) return;
            List<Unit> targets = event.unit_target;
            for (Unit target : targets) {
                if (!target.hasCondition("RaukThar")) continue;
                double max_times = getMultiplierResult("XA");
                double stimulate = getMultiplierResult("XC");
                if (getUser().getCounter().get(CounterName.VARKHA_TEL) >= max_times) continue;
                getUser().counterIncrement(CounterName.VARKHA_TEL);
                getUser().counterIncrement(CounterName.VARKHA_TEL_STIMULATE);
                if (getUser().getCounter().get(CounterName.VARKHA_TEL_STIMULATE) >= stimulate) {
                    Conditions condition = new Conditions("Stimulate : Amplify");
                    condition.setDescription("การจู่โจมสร้างความเสียหายมากขึ้น "+getMultiplierResult("XD")*100+"%, เมื่อจู่โจมจะสูญเสียสถานะนี้หนึ่งแสต็ค");
                    for (int i = 0; i <= getMultiplierResult("XB"); i++) {
                        ConditionManager.applyCondition(condition, getUser(), 10);
                    }
                }
                double ratk = getUser().getStats().get(StatType.RANGEDATTACK).getFinal();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), targets)
                                .effect(ActionEffectType.DAMAGE_PHYSICAL,ratk, 1)
                                .addActType(ActType.STRIKE, ActType.SKILL_TRIGGER)
                                .build()
                );
            }
        });

        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE)) return;
            if (!isActive()) return;
            if (event.unit_source != getUser()) return;

            if (getUser().hasCondition("Stimulate : Amplify")) {
                ConditionManager.removeOneCondition(getUser(), "Stimulate : Amplify");
                event.addAllDamageMultModifier(getMultiplierResult("XD"));
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
