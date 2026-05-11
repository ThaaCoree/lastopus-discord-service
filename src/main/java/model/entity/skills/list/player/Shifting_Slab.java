package model.entity.skills.list.player;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;

public class Shifting_Slab extends Skill {

    public static String NAME = "Shifting Slab";

    public Shifting_Slab() {
        super();
        setDescription("เมื่อพันธมิตรที่อยู่ในรัศมี XA เมตรรอบตัวผู้ใช้ถูกจู่โจม เคลื่อนย้ายพันธมิตรได้ไกลสุด XB เมตร\n" +
                "การหลบด้วย Shifting Slab จะถือว่าสำเร็จ");
        setActionType("Reaction");
        setManaCost(4);
        setCooldown(1);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*MSPD"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISTANCE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.5*MSPD"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DISTANCE);
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
