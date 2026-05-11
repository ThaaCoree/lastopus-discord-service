package main.java.model.entity.skills.list.item.defensive;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

public class Magic_Divinity extends Skill {

    public static String NAME = "Magic Divinity";

    public Magic_Divinity() {
        super();
        setDescription("บล็อกการจู่โจมเวทมนตร์หนึ่งครั้ง หยุดยั้งผลทะลุทะลวงทั้งหมดของมัน\n" +
                "สกิลนี้ไม่สามารถถูกลดคูลดาวน์จนเหลือต่ำกว่าหนึ่งเทิร์นได้");
        setActionType("Reaction");
        setManaCost(0);
        setCooldown(2);
        getPureTags().add(SkillType.DEFENSE);
        getPureTags().add(SkillType.SPELL);
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
