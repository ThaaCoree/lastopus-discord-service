package model.entity.skills.list.item.support;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class Emperors_Bound extends Skill {

    public static String NAME = "Emperor's Bound";

    public Emperors_Bound() {
        super();
        setDescription("ผูกพันธะกับหนึ่งยูนิต จากนั้นรับความเสียหายครึ่งหนึ่งแทนยูนิตนั้นจนกว่าจะสะบั้นพันธะ\n" +
                "สามารถใช้ Combined Action เพื่อสะบั้นพันธะได้\n" +
                "เมื่อใช้งาน Emperor´s Bound กับยูนิตใหม่ จะสะบั้นพันธะของยูนิตเดิม");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEFENSE);
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
