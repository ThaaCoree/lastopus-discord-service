package model.entity.skills.list.item.defensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;

public class Immortalis extends Skill {

    public static String NAME = "Immortalis";

    public Immortalis() {
        super();
        setDescription("การได้รับความเสียหายที่ทำให้พลังชีวิตต่ำกว่า 1 หน่วย จะไม่ทำให้หมดสติ แต่จะทำให้ได้รับ Exhausted หนึ่งสแต็คแทน\n" +
                "หมดสติเมื่อมี Exhausted สิบสแต็คหรือมากกว่า และไม่สามารถตื่นในการต่อสู้ได้อีกจนกว่า Exhausted จะลดลงเหลือต่ำกว่าสิบ\n" +
                "ผลลดพลังชีวิตของ Exhausted จะถูกลดเหลือ XA ต่อสแต็ค");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEFENSE);
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
