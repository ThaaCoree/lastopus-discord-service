package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import util.LogWriterUtil;

public class Stars_Unseal extends Skill {

    public static String NAME = "Stars Unseal";

    public Stars_Unseal() {
        super();
        setDescription("เผยดวงดาวทั้งฟากฟ้า หยิบยืมพลังจากมัน\n" +
                "ทันทีที่สิ้นสุดรอบเทิร์น ยูนิตศัตรูทั้งหมดที่ไม่ได้เคลื่อนไหวมากกว่า 12 เมตรในรอบเทิร์นนี้ รับความเสียหายเวท XA หน่วย ไม่สามารถถูกหลบหรือบล็อกได้");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("3.2*MATK"));
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
        LogWriterUtil.log("Stars Unseal deals "+(xa)+" magical damage", combatFlow.getTurnCount());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
