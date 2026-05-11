package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;

public class Last_Wish extends Skill {

    public static String NAME = "Last Wish";

    public Last_Wish() {
        super();
        setDescription("สามารถใช้งานโอปัส, เวทมนตร์ และมานาในระหว่างที่มานาติดลบได้\n" +
                "เมื่อใช้งานความสามารถนี้ รับความเสียหายจริง XA ของพลังชีวิตสูงสุด\n" +
                "หากทำซ้ำในขณะที่มานายังไม่กลับมาเป็น 0 หรือมากกว่า รับเพิ่มอีก 10% ทับซ้อนอย่างไม่จำกัด");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DRAWBACK);
        getSkillMultiplier().get("XA").setPercent(true);
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
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
