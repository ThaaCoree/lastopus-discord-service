package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

public class Spell_Deconstruct extends Skill {

    public static String NAME = "Spell Deconstruct";

    public Spell_Deconstruct() {
        super();
        setDescription("ถอดองค์ประกอบของเวทมนตร์และเรียบเรียงใหม่ เลือกระหว่าง ฮีลให้กับพันธมิตรหนึ่งยูนิตเป็นครึ่งหนึ่งของความเสียหายที่เวทมนตร์นั้นทำได้ หรือ สลายเวทมนตร์");
        setActionType("Combine + Reaction");
        setManaCost(6);
        setCooldown(2);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.DISPEL);
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
