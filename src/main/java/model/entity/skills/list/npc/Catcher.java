package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

public class Catcher extends Skill {

    public static String NAME = "Catcher";

    public Catcher() {
        super();
        setDescription("เมื่อเกิดการจู่โจม สามารถใช้งาน Reaction เพื่อคว้าตัวยูนิตที่กำลังถูกจู่โจมให้หลบการโจมตีนั้นได้\n" +
                "การหลบนี้จะใช้ค่า Evasion ของ Re9925\n" +
                "เพิ่มค่า Evasion XA");
        setActionType("Passive");
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        getSkillModifier().getStatModifierSafe(StatType.EVASION).setGlobalMult(xa);
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
