package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Duplicator extends Skill {

    public static String NAME = "Duplicator";

    public Duplicator() {
        super();
        setDescription("เมื่อใช้งาน, เลียนแบบการกระทำของยูนิตใดก็ตามในสนาม; หากการกระทำนั้นไม่ได้ซับซ้อนจนเกินความสามารถของผู้ใช้, สร้างภาพจำลองขึ้นมาเลียนแบบการกระทำนั้น\n" +
                "หรือสามารถใช้งาน Action เพื่อใช้สกิลนี้สร้างสิ่งเลียนแบบสิ่งของขึ้นมาแทนได้ หากไม่ได้ซับซ้อนจนเกินไปจะเลียนแบบออกมาได้อย่างสมบูรณ์");
        setActionType("Hold Action");
        setManaCost(6);
        setCooldown(3);
        setManaReservePercent(0.25);
        getPureTags().add(SkillType.OPUS);
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
