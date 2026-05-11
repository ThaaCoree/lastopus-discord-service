package model.entity.skills.list.player;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import model.type.StatType;
import util.LogWriterUtil;

public class re_by_y extends Skill {

    public static String NAME = "re by y";

    public re_by_y() {
        super();
        setDescription("หลังจากใช้สกิลที่เกี่ยวกับโอปัสของตนการโจมตีครั้งถัดไปจะสร้างความเสียหายเพิ่มอีก XA หน่วย\n" +
                "เมื่อโจมตีด้วยผลพิเศษนี้ ไม่รับความเสียหายย้อนกลับจากการโจมตีผนวกความเร็ว");
        setActionType("Passive");
        setManaReservePercent(0.15);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.3*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
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
        LogWriterUtil.log("re by y dealing extra "+ getSkillMultiplier().get("XA").getResultString() +" damage,\n" +
                        "dealing "+ (getSkillMultiplier().get("XA").getResult() + getUser().getStats().get(StatType.PHYSICALATTACK).getFinal()) +" if auto-attacking"
                , combatFlow.getTurnCount());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
