package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Totem_Strike extends Skill {

    public static String NAME = "Totem Strike";

    public Totem_Strike() {
        super();
        setDescription("ใช้งานได้เมื่อมีหลักมนตราอยู่ในสนามเท่านั้น\n" +
                "หมุนและเปลี่ยนลำดับอักษรของหลักที่อยู่ในสนามทั้งหมดเล็กน้อย เรียกกระสุนมนตราออกมาจากหลักพุ่งเข้าใส่ยูนิตศัตรูที่ใกล้ที่สุด XA นัด แต่ละนัดสร้างความเสียหายกายภาพ XB หน่วย");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.4*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
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
            double xb = getSkillMultiplier().get("XB").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .addActType(ActType.CAST)
                            .build()
            );

            for (int i=0 ;i < xa ; i++) {
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xb, 1)
                                .addActType(ActType.STRIKE)
                                .build()
                );
            }
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
