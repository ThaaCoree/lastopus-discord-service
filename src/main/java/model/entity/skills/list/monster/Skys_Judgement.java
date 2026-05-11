package model.entity.skills.list.monster;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import util.LogWriterUtil;

public class Skys_Judgement extends Skill {

    public static String NAME = "Sky's Judgement";

    public Skys_Judgement() {
        super();
        setDescription("เลือกสามยูนิตในสนาม ยูนิตแรก ห้ามสร้างความเสียหาย ยูนิตที่สอง ห้ามมอบหรือได้รับบัพและการฟื้นฟู ยูนิตที่สาม ห้ามเคลื่อนไหว\n" +
                "ทันทีที่ยูนิตดังกล่าวฝ่าฝืนกฎนี้ มันรับความเสียหายจริง XA หน่วย");
        setActionType("Action");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("2*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
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
        LogWriterUtil.log("Sky's Judgement deals "+(xa)+" true damage", combatFlow.getTurnCount());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
