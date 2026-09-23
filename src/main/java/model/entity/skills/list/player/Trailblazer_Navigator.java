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

public class Trailblazer_Navigator extends Skill {

    public static String NAME = "Trailblazer Navigator";

    public Trailblazer_Navigator() {
        super();
        setDescription("ล็อคเป้าศัตรูที่มี HP มากที่สุด จากนั้นเลือกพันธมิตรหนึ่งยูนิต\n" +
                "ยูนิตนั้นได้รับ Action ที่ใช้งานได้ทันที, การใช้งานสกิลด้วย Action ดังกล่าวจะไม่นับคูลดาวน์และไม่ใช้งานมานา/พลังชีวิต");
        setActionType("Action");
        setManaCost(8);
        setCooldown(2);
        getPureTags().add(SkillType.OPUS);
        getPureTags().add(SkillType.RESOURCE);
        getPureTags().add(SkillType.FIGHTING_STYLE);
        setManaReservePercent(0.1);
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
            double xa = getSkillMultiplier().get("XA").getResult();
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.findCondition("Glyph Shocked");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .condition(condition, duration)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
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
