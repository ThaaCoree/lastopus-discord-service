package main.java.model.entity.skills.list.item.offensive;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import util.LogWriterUtil;

public class Light_Chamber extends Skill {

    public static String NAME = "Light Chamber";

    public Light_Chamber() {
        super();
        setDescription("สร้างอาณาเขตขนาด 9x9 เมตรเป็นเวลา XA รอบเทิร์น\n" +
                "ทุกรอบเทิร์น ภาพที่ถูกสะท้อนในอาวุธประเภทกระจกจะถูกจู่โจมด้วยแสงจากรอบทิศทาง สร้างความเสียหายโดยตรง XB หน่วย\n" +
                "การจู่โจมนี้ไม่สามารถถูกหลบได้");
        setActionType("Action");
        setManaCost(0);
        setCooldown(5);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.7*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIGHT);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        LogWriterUtil.log("Light Chamber deals "+(xb)+" magical damage each turn for "+xa+" turns", combatFlow.getTurnCount());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
