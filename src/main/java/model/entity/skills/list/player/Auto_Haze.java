package model.entity.skills.list.player;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class Auto_Haze extends Skill {

    public static String NAME = "Auto Haze";

    public Auto_Haze() {
        super();
        setDescription("สามารถใช้งานพาสซีฟโอปัสเพื่อเทเลพอร์ตใน Reaction ได้โดยไม่จำเป็นต้องผสม Combined Action");
        setActionType("Passive");
        getPureTags().add(SkillType.OPUS);
        getPureTags().add(SkillType.RESOURCE);
        setManaReservePercent(0.25);
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
