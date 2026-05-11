package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;
import util.LogWriterUtil;

public class Condensed_Sun_Ray extends Skill {

    public static String NAME = "Condensed Sun Ray";

    public Condensed_Sun_Ray() {
        super();
        setDescription("เมื่อใช้งาน เริ่มร่าย Ceremony ที่สิ้นสุดลงตอนเริ่มเทิร์นหน้าของตนเอง\n" +
                "เมื่อร่ายเสร็จสิ้น เลือกพื้นที่ 2x2 เมตร รวบรวมลำแสงจากดวงอาทิตย์ลงมายังพื้นที่เป้าหมาย สร้างความเสียหายจริง XA หน่วยให้กับยูนิตทั้งหมดในพื้นที่\n" +
                "สกิลนี้สามารถถูกใช้งานได้ในตอนกลางวันเท่านั้น");
        setActionType("Turn");
        setManaCost(19);
        setCooldown(6);
        getSkillMultiplier().put("XA",new SkillMultiplier("4.1*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.CEREMONY);
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
                            .effect(ActionEffectType.DAMAGE_TRUE, xa, 1)
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
