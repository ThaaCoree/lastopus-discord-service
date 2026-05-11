package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;

public class Orbital_Strike extends Skill {

    public static String NAME = "Orbital Strike";

    public Orbital_Strike() {
        super();
        setDescription("เริ่มร่าย Ceremony ที่สิ้นสุดลงเมื่อเริ่มต้นเทิร์นหน้าของตัวเอง\n" +
                "กำหนดพื้นที่ 3x3 เมตรในแผนที่ เลือกจุดที่มองไม่เห็นได้ เมื่อผ่านไปครบหนึ่งเทิร์น สร้างความเสียหายโดยตรง XA หน่วยให้กับทุกยูนิตที่อยู่ภายใน");
        setActionType("Turn");
        setManaCost(36);
        setCooldown(6);
        getSkillMultiplier().put("XA",new SkillMultiplier("4.8*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PURE);
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
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PURE, xa, 1)
                            .addActType(ActType.CAST, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
