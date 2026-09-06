package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.CounterName;
import model.type.SkillType;

import java.util.List;

public class Edelweiss_End extends Skill {

    public static String NAME = "Edelweiss End";

    public Edelweiss_End() {
        super();
        setDescription("เมื่อใช้งานมอบ [The Forgotten Pages] ให้1ใบ\n" +
                "\n" +
                "ถ้าหากมี [The Forgotten Pages] ครบ6ใบ เมื่อเพื่อนร่วมทีมคนสุดท้ายในทีมหมดสภาพการต่อสู้ Twelve จะใช้แรงเฮือกสุดท้ายลุกขึ้นมากล่าวบทร่ายยืมพลังจากจันทรา สร้างทุ่งดอกเอเดลไวส์ที่อยู่กลางน้ำขึ้นมา\n" +
                "ฟื้นฟูพลังชีวิตให้พันธมิตรทั้งหมดจนเต็มและปลุกให้ตื่น พันธมิตรที่ถูกปลุกด้วยสกิลนี้จะไม่สูญเสีย Action จากอาการหมดสติ หากมีพันธมิตรที่ตาย เลือกชุบชีวิตหนึ่งยูนิต จากนั้น Twelve หมดสภาพการต่อสู้โดยไม่สามารถปลุกให้ตื่นได้อีกจนกว่าจะจบการต่อสู้ พร้อมทั้งลบ [The Forgotten Pages] ทั้งหมดออกไป");
        setActionType("Turn");
        setManaCost(20);
        setCooldown(2);
        setManaReservePercent(0.4);
        getPureTags().add(SkillType.OPUS);
        getPureTags().add(SkillType.RESOURCE);
        getPureTags().add(SkillType.RECOVERY);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Get Pages", "Edelweiss End");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
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
        if (skillTarget.getTarget(0).contains("Get Pages")) {
            getUser().counterIncrement(CounterName.THE_FORGOTTEN_PAGES);
        }

        if (skillTarget.getTarget(0).contains("Edelweiss End")) {
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getOtherAllies(combatFlow))
                            .effect(ActionEffectType.HEALTH_RECOVER, 12121212, 1)
                            .addActType(ActType.CAST, ActType.HEALTH_RECOVER)
                            .build()
            );

            getUser().counterSet(CounterName.THE_FORGOTTEN_PAGES, 0);
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
//                                ActionEvent.builder(getName(), getUser(), targets)
//                                        .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                                        .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                                        .build()
//                        );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}
