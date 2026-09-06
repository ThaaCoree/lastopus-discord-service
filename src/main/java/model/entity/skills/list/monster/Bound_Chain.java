package model.entity.skills.list.monster;

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

public class Bound_Chain extends Skill {

    public static String NAME = "Bound Chain";

    public Bound_Chain() {
        super();
        setDescription("Imprison ศัตรู XA ยูนิตด้วยโซ่พลังชีวิต XB หน่วยเป็นเวลา 2 รอบเทิร์น ไม่สามารถใช้งานกับยูนิตเดิมได้จนกว่ารีเซ็ตการเลือกเป้าหมาย\n" +
                "รีเซ็ตการเลือกเป้าหมายเมื่อยูนิตทั้งหมดในสนามจะเคยถูก Bound Chain หรือเคยหมดสติแล้ว");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("4"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.025*HP"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.IMPRISON);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {
            Conditions condition = combatFlow.findCondition("Imprison");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, 2)
                            .addActType(ActType.CONDITION_GIVEN, ActType.CAST)
                            .build()
            );
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
