package model.entity.skills.list.item.offensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class Even_In_Death extends Skill {

    public static String NAME = "Even in Death";

    public Even_In_Death() {
        super();
        setDescription("ปลุกพันธมิตรที่หมดสภาพต่อสู้ให้ลุกขึ้นมาจู่โจมหนึ่งครั้ง ความเสียหายของการโจมตีครั้งนี้จะมีค่าเท่ากับความเสียหายสูงสุดที่ยูนิตนี้เคยกระทำในการต่อสู้");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(2);
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
